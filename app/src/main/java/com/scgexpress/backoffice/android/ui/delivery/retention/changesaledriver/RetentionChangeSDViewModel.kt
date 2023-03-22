package com.scgexpress.backoffice.android.ui.delivery.retention.changesaledriver

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.db.entity.masterdata.TblAuthUsers
import com.scgexpress.backoffice.android.db.entity.masterdata.TblScgBranch
import com.scgexpress.backoffice.android.repository.masterdata.DataRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import javax.inject.Inject

class RetentionChangeSDViewModel@Inject constructor(
    application: Application,
    private val dataRepo: DataRepository
) : RxAndroidViewModel(application)   {

    var masterDataBranchList: List<TblScgBranch> = emptyList()
    var masterDataSDList: List<TblAuthUsers> = emptyList()

    private val _masterDataBranch = MutableLiveData<List<TblScgBranch>>()
    val masterDataBranch: LiveData<List<TblScgBranch>>
        get() = _masterDataBranch

    private val _masterDataSD = MutableLiveData<List<TblAuthUsers>>()
    val masterDataSD: LiveData<List<TblAuthUsers>>
        get() = _masterDataSD

    fun loadData(){
        getMasterDataBranch()
    }

    fun getMasterDataSD(branchId: String){
        dataRepo.getScgSD(branchId).subscribe { list ->
            _masterDataSD.value = list
        }.also {
            addDisposable(it)
        }
    }

    private fun getMasterDataBranch(){
        dataRepo.getScgBranch().subscribe{ list ->
            _masterDataBranch.value = list
        }.also {
            addDisposable(it)
        }
    }
}