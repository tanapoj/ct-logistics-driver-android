package com.scgexpress.backoffice.android.repository.delivery

import com.scgexpress.backoffice.android.api.DeliveryService
import com.scgexpress.backoffice.android.model.*
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Deprecated("legacy delivery")
@Singleton
class DeliveryNetworkRepository @Inject constructor(private val service: DeliveryService) {

    companion object {
        private const val type: String = "OFD"
        private const val limit: String = "100"
        private const val parcelLimit: String = "10"
        private const val withBookings: Boolean = true
    }

    fun getOfdManifests(from: String, to: String): Flowable<Manifests> {
        return service.getManifests(type, from, to, limit, withBookings)
    }

    fun getManifests(from: String, to: String): Flowable<ArrayList<Manifest>> {
        return service.getManifests(type, from, to, limit, withBookings).map(this::parseToManifestArrayList)
    }

    fun getManifestItems(manifestID: String): Flowable<ManifestDetail> {
        return service.getManifestDetails(manifestID, parcelLimit, withBookings).map(this::changeTrackingNumberFormat)
    }

    fun getTrackingInfo(manifestID: String): Flowable<List<TrackingInfo>> {
        return service.getManifestDetails(manifestID, parcelLimit, withBookings).map(this::parseToTrackingArrayList)
    }

    fun getBookings(from: String, to: String): Flowable<ArrayList<BookingInfo>> {
        return service.getManifests(type, from, to, limit, withBookings).map(this::parseToNewBookingArrayList)
    }

    fun getBookingInfo(manifestID: String): Flowable<List<BookingInfo>> {
        return service.getManifestDetails(manifestID, parcelLimit, withBookings).map(this::parseToBookingArrayList)
    }

    fun getBookingItems(bookingID: String): Flowable<List<TrackingNoByBooking>> {
        return service.getBookingItems(bookingID).map(this::changeTrackingNumberFormat)
    }

    fun getDelivery(from: String, to: String): Flowable<Manifests> {
        return service.getManifests(type, from, to, limit, withBookings)
    }

    fun getTracking(trackingNumber: String): Flowable<TrackingInfo> {
        return service.getTrackingDetails(trackingNumber).map(this::changeTrackingNumberFormat)
    }

    fun createManifest(modelList: ArrayList<DeliveryOfdCreate>): Flowable<String> {
        return service.createManifest(DeliveryOfdCreateList(modelList))
    }

    fun scanOfd(manifestID: String, items: DeliveryOfdParcelList): Flowable<ArrayList<DeliveryOfdParcelResponse>> {
        return service.addParcels(manifestID, items).map(this::parseToManifestArrayList)
    }

    fun deleteOfdScan(
        manifestID: String,
        items: DeliveryOfdParcelList
    ): Flowable<ArrayList<DeliveryOfdParcelResponse>> {
        return service.deleteParcels(manifestID, items).map(this::parseToManifestArrayList)
    }

    fun getManifestItemScanned(manifestID: String): Flowable<ArrayList<DeliveryOfdParcelResponse>> {
        return service.getManifestItemScanned(manifestID).map(this::parseToManifestArrayList)
    }

    fun getManifestHeader(manifestID: String): Flowable<Manifest> {
        return service.getManifestHeader(manifestID).map(this::parseToManifest)
    }

    fun acceptBooking(bookingId: String, assignmentId: String, manifestID: String): Flowable<ApiResponse> {
        val map: HashMap<String, Any> = hashMapOf()
        map["bookingID"] = bookingId ?: ""
        map["assignmentID"] = assignmentId ?: ""
        map["manifestID"] = manifestID

        return service.acceptBooking(map)
    }

    fun rejectBooking(
        bookingId: String, assignmentId: String, idSubCancelReason: String
        , idCancelReason: String, note: String
    ): Flowable<ApiResponse> {
        val map: HashMap<String, Any> = hashMapOf()
        map["bookingID"] = bookingId ?: ""
        map["assignmentID"] = assignmentId ?: ""
        map["id_sub_cancel_reason"] = idSubCancelReason ?: ""
        map["id_cancel_reason"] = idCancelReason ?: ""
        map["note"] = note ?: ""

        return service.rejectBooking(map)
    }

    fun addPhoto(trackingNumber: String, photos: TrackingPhotoList): Flowable<String> {
        return service.addPhoto(trackingNumber, photos)
    }

    fun reStatus(manifestID: String, body: OfdItemReStatus): Flowable<String> {
        return service.reStatus(manifestID, body)
    }

    private fun parseToManifestArrayList(delivery: Manifests): ArrayList<Manifest> {
        return delivery.manifestList
    }

    private fun parseToNewBookingArrayList(delivery: Manifests): ArrayList<BookingInfo> {
        return delivery.newBookingList
    }

    private fun parseToManifestArrayList(delivery: DeliveryOfdParcelResponseList): ArrayList<DeliveryOfdParcelResponse> {
        return changeTrackingNumberFormat(delivery.items)
    }

    private fun changeTrackingNumberFormat(data: ManifestDetail): ManifestDetail {
        try {
            val trackingList = data.trackingList
            for (item: TrackingInfo in trackingList) {
                changeTrackingNumberFormat(item)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return data
    }

    private fun parseToTrackingArrayList(manifestDetail: ManifestDetail): List<TrackingInfo> {
        try {
            val trackingList = manifestDetail.trackingList
            for (item: TrackingInfo in trackingList) {
                changeTrackingNumberFormat(item)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return manifestDetail.trackingList
    }

    private fun parseToBookingArrayList(manifestDetail: ManifestDetail): List<BookingInfo> {
        return manifestDetail.bookingList
    }

    private fun parseToManifest(data: ManifestHeader): Manifest {
        return data.manifest
    }

    private fun changeTrackingNumberFormat(trackingInfo: TrackingInfo): TrackingInfo {
        if (trackingInfo.trackingNumber != null) {
            trackingInfo.trackingNumber = trackingInfo.trackingNumber!!.replace("A", "") ?: ""
        }
        return trackingInfo
    }

    private fun changeTrackingNumberFormat(items: ArrayList<DeliveryOfdParcelResponse>): ArrayList<DeliveryOfdParcelResponse> {
        for (item: DeliveryOfdParcelResponse in items) {
            if (item.trackingId != null) {
                item.trackingId = item.trackingId.replace("A", "") ?: ""
            }
        }

        return items
    }

    private fun changeTrackingNumberFormat(items: List<TrackingNoByBooking>): List<TrackingNoByBooking> {
        for (item: TrackingNoByBooking in items) {
            if (item.trackingNumber != null) {
                item.trackingNumber = item.trackingNumber!!.replace("A", "") ?: ""
            }
        }

        return items
    }
}