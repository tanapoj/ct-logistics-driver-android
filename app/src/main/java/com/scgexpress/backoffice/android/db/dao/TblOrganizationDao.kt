package com.scgexpress.backoffice.android.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scgexpress.backoffice.android.db.entity.masterdata.TblOrganization
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface TblOrganizationDao {
    companion object {
        private const val TABLE_NAME: String = "tbl_organization"
    }

    @get:Query("SELECT * FROM tbl_organization ORDER BY organization_id ASC")
    val organizationItems: Flowable<List<TblOrganization>>

    @Query("SELECT * FROM tbl_organization WHERE customer_code = :customerCode")
    fun getOrganization(customerCode: String): Single<TblOrganization?>
}