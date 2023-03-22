package com.scgexpress.backoffice.android.api

import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.annotation.MockResponse
import com.scgexpress.backoffice.android.model.*
import com.scgexpress.backoffice.android.model.delivery.*
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.*

interface DeliveryService {
    companion object {
        private const val PARAMS_TASK_ID: String = "task_id"


        private const val PARAMS_TYPE: String = "type"
        private const val PARAMS_FROM: String = "from"
        private const val PARAMS_TO: String = "to"
        private const val PARAMS_LIMIT: String = "limit"
        private const val PARAMS_WITH_BOOKING: String = "withBookings"

        private const val PARAMS_BOOKING_ID: String = "bookingID"
        private const val PARAMS_MANIFEST_ID: String = "manifestID"
        private const val PARAMS_PARCEL_LIMIT: String = "parcelLimit"
        private const val PARAMS_TRACKING_NUMBER: String = "trackingNumber"
    }


    @GET("delivery/tasks/{$PARAMS_TASK_ID}")
    @MockResponse(R.raw.get_delivery_task_in_progress)
    fun getDeliveryTask(@Path(PARAMS_TASK_ID) id: String): Single<DeliveryTask>


    @GET("delivery/tasks")
    @MockResponse(R.raw.get_delivery_tasks)
    fun getDeliveryTasks(): Flowable<DeliveryTaskList>

    @GET("delivery/ofd/submit")
    @MockResponse(R.raw.get_delivery_ofd_submit)
    fun submitOfd(@Body body: OfdSubmit): Flowable<OfdSubmitResponse>

    @POST("delivery/submit")
    fun submitSentDelivery(@Body body: SentSubmit): Flowable<SentSubmitResponse>


    @GET("manifests")
    fun getManifests(
        @Query(PARAMS_TYPE) type: String,
        @Query(PARAMS_FROM) from: String,
        @Query(PARAMS_TO) to: String,
        @Query(PARAMS_LIMIT) limit: String,
        @Query(PARAMS_WITH_BOOKING) withBookings: Boolean
    ): Flowable<Manifests>

    @GET("manifests/{manifestID}")
    fun getManifestDetails(
        @Path(PARAMS_MANIFEST_ID) manifestID: String,
        @Query(PARAMS_PARCEL_LIMIT) parcelLimit: String,
        @Query(PARAMS_WITH_BOOKING) withBookings: Boolean
    ): Flowable<ManifestDetail>

    @GET("parcels/{trackingNumber}")
    fun getTrackingDetails(@Path(PARAMS_TRACKING_NUMBER) trackingNumber: String): Flowable<TrackingInfo>

    @GET("getManifestItemList/{manifestID}")
    fun getManifestItemScanned(@Path(PARAMS_MANIFEST_ID) manifestID: String): Flowable<DeliveryOfdParcelResponseList>

    @GET("manifestHeader/{manifestID}")
    fun getManifestHeader(@Path(PARAMS_MANIFEST_ID) manifestID: String): Flowable<ManifestHeader>

    @GET("getTrackingNoByBooking/{bookingID}")
    fun getBookingItems(@Path(PARAMS_BOOKING_ID) bookingID: String): Flowable<List<TrackingNoByBooking>>

    @PUT("reStatus/{manifestID}")
    fun reStatus(@Path(PARAMS_MANIFEST_ID) manifestID: String, @Body body: OfdItemReStatus): Flowable<String>

    @POST("manifests")
    fun createManifest(@Body body: DeliveryOfdCreateList): Flowable<String>

    @PUT("manifests/{manifestID}/parcels")
    fun addParcels(@Path(PARAMS_MANIFEST_ID) manifestID: String, @Body body: DeliveryOfdParcelList): Flowable<DeliveryOfdParcelResponseList>

    @HTTP(method = "DELETE", path = "manifests/{manifestID}/parcels", hasBody = true)
    fun deleteParcels(@Path(PARAMS_MANIFEST_ID) manifestID: String, @Body body: DeliveryOfdParcelList): Flowable<DeliveryOfdParcelResponseList>

    @POST("acceptPickupTask")
    fun acceptBooking(@Body body: HashMap<String, Any>): Flowable<ApiResponse>

    @POST("cancelBooking")
    fun rejectBooking(@Body body: HashMap<String, Any>): Flowable<ApiResponse>

    @POST("parcelPhotos/{trackingNumber}")
    fun addPhoto(@Path(PARAMS_TRACKING_NUMBER) trackingNumber: String, @Body body: TrackingPhotoList): Flowable<String>
}