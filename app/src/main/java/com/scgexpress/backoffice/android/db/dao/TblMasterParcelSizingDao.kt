package com.scgexpress.backoffice.android.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scgexpress.backoffice.android.db.entity.masterdata.TblMasterParcelSizing
import io.reactivex.Flowable

@Dao
interface TblMasterParcelSizingDao {
    companion object {
        private const val TABLE_NAME: String = "tbl_master_parcel_sizing"
    }

    @get:Query("SELECT * FROM tbl_master_parcel_sizing ORDER BY id ASC")
    val parcelSizingItems: Flowable<List<TblMasterParcelSizing>>

    @Query("SELECT * FROM tbl_master_parcel_sizing WHERE id = :sizingID")
    fun getparcelSizing(sizingID: String): Flowable<TblMasterParcelSizing>
}