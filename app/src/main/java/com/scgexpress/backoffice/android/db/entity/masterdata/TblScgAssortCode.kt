package com.scgexpress.backoffice.android.db.entity.masterdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "tbl_scg_assort_code")
data class TblScgAssortCode(
    @PrimaryKey @Json(name = "zipcode") @SerializedName("zipcode") @ColumnInfo(name = "zipcode") var zipcode: String = "",
    @Json(name = "id_region") @SerializedName("id_region") @ColumnInfo(name = "id_region") var idRegion: Int = 1,
    @Json(name = "assort_code") @SerializedName("assort_code") @ColumnInfo(name = "assort_code") var assortCode: String? = "",
    @Json(name = "assort_group") @SerializedName("assort_group") @ColumnInfo(name = "assort_group") var assortGroup: String? = "",
    @Json(name = "address_1") @SerializedName("address_1") @ColumnInfo(name = "address_1") var address1: String? = "",
    @Json(name = "address_2") @SerializedName("address_2") @ColumnInfo(name = "address_2") var address2: String? = "",
    @Json(name = "address_3") @SerializedName("address_3") @ColumnInfo(name = "address_3") var address3: String? = ""
) {
    @Ignore
    constructor() : this("0")
}