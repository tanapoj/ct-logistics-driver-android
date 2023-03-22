package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_api_token")
data class TblScgApiToken(
    @PrimaryKey @Json(name = "id") @SerializedName("id") @ColumnInfo(name = "id") var id: Int = 0,
    @Json(name = "user_id") @SerializedName("user_id") @ColumnInfo(name = "user_id") var userId: Int = 0,
    @Json(name = "code") @SerializedName("code") @ColumnInfo(name = "code") var code: String? = "0",
    @Json(name = "status_expired") @SerializedName("status_expired") @ColumnInfo(name = "status_expired") var statusExpired: String? = "N",
    @Json(name = "date_created") @SerializedName("date_created") @ColumnInfo(name = "date_created") var dateCreated: String? = "",
    @Json(name = "date_modified") @SerializedName("date_modified") @ColumnInfo(name = "date_modified") var dateModified: String? = ""
) {
    @Ignore
    constructor() : this(0)
}