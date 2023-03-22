package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_consignment_del")
data class TblScgConsignmentDel(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "consignment_type") @SerializedName("consignment_type") @ColumnInfo(name = "consignment_type") var consignmentType: String? = "individual",
    @Json(name = "consignment_status") @SerializedName("consignment_status") @ColumnInfo(name = "consignment_status") var consignmentStatus: Int = 64,
    @Json(name = "consignment_drop_type") @SerializedName("consignment_drop_type") @ColumnInfo(name = "consignment_drop_type") var consignmentDropType: Int = 88,
    @Json(name = "consignment_description") @SerializedName("consignment_description") @ColumnInfo(name = "consignment_description") var consignmentDescription: String? = "",
    @Json(name = "consignment_note") @SerializedName("consignment_note") @ColumnInfo(name = "consignment_note") var consignmentNote: String? = "",
    @Json(name = "job_type") @SerializedName("job_type") @ColumnInfo(name = "job_type") var jobType: Int = 60,
    @Json(name = "registered_channel") @SerializedName("registered_channel") @ColumnInfo(name = "registered_channel") var registeredChannel: Int = 63,
    @Json(name = "service_require") @SerializedName("service_require") @ColumnInfo(name = "service_require") var serviceRequire: Int = 57,
    @Json(name = "user_id") @SerializedName("user_id") @ColumnInfo(name = "user_id") var userId: Int = 0,
    @Json(name = "user_consignment_code") @SerializedName("user_consignment_code") @ColumnInfo(name = "user_consignment_code") var userConsignmentCode: String? = "",
    @Json(name = "user_order_date") @SerializedName("user_order_date") @ColumnInfo(name = "user_order_date") var userOrderDate: String? = "",
    @Json(name = "user_commit_date") @SerializedName("user_commit_date") @ColumnInfo(name = "user_commit_date") var userCommitDate: String? = "",
    @Json(name = "user_description") @SerializedName("user_description") @ColumnInfo(name = "user_description") var userDescription: String? = "",
    @Json(name = "user_note") @SerializedName("user_note") @ColumnInfo(name = "user_note") var userNote: String? = "",
    @Json(name = "sender_code") @SerializedName("sender_code") @ColumnInfo(name = "sender_code") var senderCode: String? = "",
    @Json(name = "sender_name") @SerializedName("sender_name") @ColumnInfo(name = "sender_name") var senderName: String? = "",
    @Json(name = "sender_tel") @SerializedName("sender_tel") @ColumnInfo(name = "sender_tel") var senderTel: String? = "",
    @Json(name = "sender_address") @SerializedName("sender_address") @ColumnInfo(name = "sender_address") var senderAddress: String? = "",
    @Json(name = "sender_zipcode") @SerializedName("sender_zipcode") @ColumnInfo(name = "sender_zipcode") var senderZipcode: String? = "",
    @Json(name = "sender_identity_fname") @SerializedName("sender_identity_fname") @ColumnInfo(name = "sender_identity_fname") var senderIdentityFname: String? = "",
    @Json(name = "sender_identity_lname") @SerializedName("sender_identity_lname") @ColumnInfo(name = "sender_identity_lname") var senderIdentityLname: String? = "",
    @Json(name = "sender_identity_idcardnumber") @SerializedName("sender_identity_idcardnumber") @ColumnInfo(name = "sender_identity_idcardnumber") var senderIdentityIdcardnumber: String? = "",
    @Json(name = "recipient_code") @SerializedName("recipient_code") @ColumnInfo(name = "recipient_code") var recipientCode: String? = "",
    @Json(name = "recipient_name") @SerializedName("recipient_name") @ColumnInfo(name = "recipient_name") var recipientName: String? = "",
    @Json(name = "recipient_tel") @SerializedName("recipient_tel") @ColumnInfo(name = "recipient_tel") var recipientTel: String? = "",
    @Json(name = "recipient_contact_name") @SerializedName("recipient_contact_name") @ColumnInfo(name = "recipient_contact_name") var recipientContactName: String? = "",
    @Json(name = "recipient_contact_tel") @SerializedName("recipient_contact_tel") @ColumnInfo(name = "recipient_contact_tel") var recipientContactTel: String? = "",
    @Json(name = "recipient_address") @SerializedName("recipient_address") @ColumnInfo(name = "recipient_address") var recipientAddress: String? = "",
    @Json(name = "recipient_zipcode") @SerializedName("recipient_zipcode") @ColumnInfo(name = "recipient_zipcode") var recipientZipcode: String? = "",
    @Json(name = "parcels_total") @SerializedName("parcels_total") @ColumnInfo(name = "parcels_total") var parcelsTotal: Int = 0,
    @Json(name = "parcels_status_info") @SerializedName("parcels_status_info") @ColumnInfo(name = "parcels_status_info") var parcelsStatusInfo: String? = "",
    @Json(name = "cod_amount") @SerializedName("cod_amount") @ColumnInfo(name = "cod_amount") var codAmount: String? = "",
    @Json(name = "cod_accept") @SerializedName("cod_accept") @ColumnInfo(name = "cod_accept") var codAccept: Int = 0,
    @Json(name = "ccod_accept") @SerializedName("ccod_accept") @ColumnInfo(name = "ccod_accept") var ccodAccept: Int = 0,
    @Json(name = "insurance_accept") @SerializedName("insurance_accept") @ColumnInfo(name = "insurance_accept") var insuranceAccept: Int = 0,
    @Json(name = "insurance_amount") @SerializedName("insurance_amount") @ColumnInfo(name = "insurance_amount") var insuranceAmount: String? = "",
    @Json(name = "document_return_request") @SerializedName("document_return_request") @ColumnInfo(name = "document_return_request") var documentReturnRequest: Int = 0,
    @Json(name = "product_return_request") @SerializedName("product_return_request") @ColumnInfo(name = "product_return_request") var productReturnRequest: Int = 0,
    @Json(name = "document_return_issue_date") @SerializedName("document_return_issue_date") @ColumnInfo(name = "document_return_issue_date") var documentReturnIssueDate: String? = "",
    @Json(name = "product_return_issue_date") @SerializedName("product_return_issue_date") @ColumnInfo(name = "product_return_issue_date") var productReturnIssueDate: String? = "",
    @Json(name = "return_complete_date") @SerializedName("return_complete_date") @ColumnInfo(name = "return_complete_date") var returnCompleteDate: String? = "",
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_commited") @SerializedName("date_commited") @ColumnInfo(name = "date_commited") var dateCommited: String? = "",
    @Json(name = "date_delivered") @SerializedName("date_delivered") @ColumnInfo(name = "date_delivered") var dateDelivered: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = "",
    @Json(name = "user_deleted") @SerializedName("user_deleted") @ColumnInfo(name = "user_deleted") var userDeleted: Int = 0,
    @Json(name = "date_deleted") @SerializedName("date_deleted") @ColumnInfo(name = "date_deleted") var dateDeleted: String? = "",
    @Json(name = "status_migrate_to_c2v2") @SerializedName("status_migrate_to_c2v2") @ColumnInfo(name = "status_migrate_to_c2v2") var statusMigrateToC2v2: String? = "N"
) {
    @Ignore
    constructor() : this(0)
}