package com.scgexpress.backoffice.android.repository.pickup

import android.util.Log
import com.scgexpress.backoffice.android.api.MasterDataService
import com.scgexpress.backoffice.android.common.isAllowCod
import com.scgexpress.backoffice.android.db.dao.MasterDataDao
import com.scgexpress.backoffice.android.db.entity.masterdata.*
import com.scgexpress.backoffice.android.model.MasterData
import com.scgexpress.backoffice.android.model.MasterDataVersion
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject
constructor(private val dao: MasterDataDao, private val service: MasterDataService) {

    private val TAG = "-pickup_task_repo"

}