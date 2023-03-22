package com.scgexpress.backoffice.android.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scgexpress.backoffice.android.db.entity.masterdata.TblMasterServiceTypeLevel3
import io.reactivex.Flowable

@Dao
interface TblMasterServiceTypeLevel3Dao {
    companion object {
        private const val TABLE_NAME: String = "tbl_master_service_type_level_3"
    }

    @get:Query("SELECT * FROM tbl_master_service_type_level_3 ORDER BY id ASC")
    val serviceTypeLevel3Items: Flowable<List<TblMasterServiceTypeLevel3>>

    @Query("SELECT * FROM tbl_master_service_type_level_3 WHERE id = :serviceTypeID")
    fun getServiceType(serviceTypeID: String): Flowable<TblMasterServiceTypeLevel3>
}