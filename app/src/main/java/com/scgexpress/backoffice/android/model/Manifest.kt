package com.scgexpress.backoffice.android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Manifest(
        @SerializedName("id") val id: String? = "",
        @SerializedName("code") val barcode: String? = "",
        @SerializedName("status_deleted") val statusDeleted: String? = "",
        @SerializedName("status_active") val statusActive: String? = "",
        @SerializedName("status_completed") val statusCompleted: String? = "",
        @SerializedName("id_manifest_type") val idManifestType: String? = "",
        @SerializedName("id_issued_branch") val idIssuedBranch: String? = "",
        @SerializedName("id_departure_branch") val idDepartureBranch: String? = "",
        @SerializedName("id_arrival_branch") val idArrivalBranch: String? = "",
        @SerializedName("name_vehicle") val nameVehicle: String? = "",
        @SerializedName("name_driver") val nameDriver: String? = "",
        @SerializedName("no_of_items_total") val noOfItemsTotal: String? = "",
        @SerializedName("no_of_items_depart") val noOfItemsDepart: String? = "",
        @SerializedName("no_of_items_arrival") val noOfItemsArrival: String? = "",
        @SerializedName("no_of_items_delivered") val noOfItemsDelivered: String? = "",
        @SerializedName("no_of_items_retention") val noOfItemsRetention: String? = "",
        @SerializedName("no_of_items_irregular") val noOfItemsIrregular: String? = "",
        @SerializedName("user_created") val userCreated: String? = "",
        @SerializedName("user_deleted") val userDeleted: String? = "",
        @SerializedName("date_created") val dateCreated: String? = "",
        @SerializedName("date_completed") val dateCompleted: String? = "",
        @SerializedName("last_modified") val lastModified: String? = "",
        @SerializedName("bookings_total") val bookingsTotal: String? = "",
        @SerializedName("bookings_done") val bookingsDone: String? = ""

        /*@SerializedName("id") val id: String = "",
        @SerializedName("barcode") val barcode: String = "",
        @SerializedName("status_deleted") val statusDeleted: String = "",
        @SerializedName("status_completed") val statusCompleted: String = "",
        @SerializedName("branch_origin") val branchOrigin: String = "",
        @SerializedName("branch_destination") val branchDestination: String = "",
        @SerializedName("no_of_received_items") val noOfReceivedItems: String = "",
        @SerializedName("no_of_total_items") val noOfTotalItems: String = "",
        @SerializedName("driver") val driver: String = "",
        @SerializedName("vehicle") val vehicle: String = "",
        @SerializedName("user_created") val userCreated: String = "",
        @SerializedName("user_deleted") val userDeleted: String = "",
        @SerializedName("date_created") val dateCreated: String = "",
        @SerializedName("date_completed") val dateCompleted: String = "",
        @SerializedName("last_modified") val lastModified: String = "",
        @SerializedName("status_neko_push_scan_out") val statusNekoPushScanOut: String = "",
        @SerializedName("code_neko_push_scan_out") val codeNekoPushScanOut: String = "",
        @SerializedName("date_neko_push_scan_out") val dateNekoPushScanOut: String = "",
        @SerializedName("status_neko_push_scan_in") val statusNekoPushScanIn: String = "",
        @SerializedName("code_neko_push_scan_in") val codeNekoPushScanIn: String = "",
        @SerializedName("date_neko_push_scan_in") val dateNekoPushScanIn: String = "",
        @SerializedName("no_of_delivered_items") val noOfDeliveredItems: String = "",
        @SerializedName("no_of_retention_items") val noOfRetentionItems: String = "",
        @SerializedName("status_migrate_to_c2v2") val statusMigrateToC2v2: String = "",
        @SerializedName("bookings_total") val bookingsTotal: String = "",
        @SerializedName("bookings_done") val bookingsDone: String = ""*/


        /*@SerializedName("id") val id: String = "",
        @SerializedName("status_test") val statusTest: String = "",
        @SerializedName("status_deleted") val statusDeleted: String = "",
        @SerializedName("status_active") val statusActive: String = "",
        @SerializedName("status_arrived") val statusArrived: String = "",
        @SerializedName("status_scan_out_destination") val statusScanOutDestination: String = "",
        @SerializedName("status_ofd_delivered") val statusOfdDelivered: String = "",
        @SerializedName("status_ofd_retention") val statusOfdRetention: String = "",
        @SerializedName("status_successful_push_to_neko") val statusSuccessfulPushToNeko: String = "",
        @SerializedName("code_response_from_neko") val codeResponseFromNeko: String = "",
        @SerializedName("datetime_push_to_neko") val datetimePushToNeko: String = "",
        @SerializedName("id_manifest_sheet") val idManifestSheet: String = "",
        @SerializedName("tracking_number") val trackingNumber: String = "",
        @SerializedName("id_parcel") val idParcel: String = "",
        @SerializedName("id_data_source") val idDataSource: String = "",
        @SerializedName("id_status") val idStatus: String = "",
        @SerializedName("id_service_type_level_1") val idServiceTypeLevel1: String = "",
        @SerializedName("id_service_type_level_2") val idServiceTypeLevel2: String = "",
        @SerializedName("id_service_type_level_3") val idServiceTypeLevel3: String = "",
        @SerializedName("id_parcel_sizing") val idParcelSizing: String = "",
        @SerializedName("id_irregular_type") val idIrregularType: String = "",
        @SerializedName("id_retention_reason") val idRetentionReason: String = "",
        @SerializedName("id_return_reason") val idReturnReason: String = "",
        @SerializedName("id_neko_billing") val idNekoBilling: String = "",
        @SerializedName("id_neko_tracking") val idNekoTracking: String = "",
        @SerializedName("id_reference_1") val idReference1: String = "",
        @SerializedName("id_reference_2") val idReference2: String = "",
        @SerializedName("id_reference_3") val idReference3: String = "",
        @SerializedName("id_reference_4") val idReference4: String = "",
        @SerializedName("id_reference_5") val idReference5: String = "",
        @SerializedName("amount_1") val amount1: String = "",
        @SerializedName("amount_2") val amount2: String = "",
        @SerializedName("amount_3") val amount3: String = "",
        @SerializedName("note_1") val note1: String = "",
        @SerializedName("note_2") val note2: String = "",
        @SerializedName("id_created_user") val idCreatedUser: String = "",
        @SerializedName("id_last_modified_user") val idLastModifiedUser: String = "",
        @SerializedName("datetime_created") val datetimeCreated: String = "",
        @SerializedName("datetime_last_modified") val datetimeLastModified: String = "",
        @SerializedName("datetime_input") val datetimeInput: String = "",
        @SerializedName("recipient_address") val recipientAddress: String = "",
        @SerializedName("recipient_tel") val recipientTel: String = "",
        @SerializedName("cod_fee_amount") val codFeeAmount: String = "",
        @SerializedName("bookings_total") val bookingsTotal: String = "",
        @SerializedName("bookings_done") val bookingsDone: String = ""*/
) : Serializable

fun Manifest.isNewOfd() = try {
    noOfItemsTotal!!.toInt() == 0
} catch (e: Exception) {
    false
}

fun Manifest.isIncompleteOfd() = try {
    noOfItemsTotal!!.toInt() > 0 && getRemainsOfd() > 0
} catch (e: Exception) {
    false
}

fun Manifest.isCompletedOfd() = try {
    noOfItemsTotal!!.toInt() > 0 && getRemainsOfd() == 0
} catch (e: Exception) {
    false
}

fun Manifest.getRemainsOfd() = try {
    noOfItemsTotal!!.toInt() - noOfItemsRetention!!.toInt() - noOfItemsDelivered!!.toInt()
} catch (e: Exception) {
    0
}

fun Manifest.getRemainsPickup() = try {
    bookingsTotal!!.toInt() - bookingsDone!!.toInt()
} catch (e: Exception) {
    0
}