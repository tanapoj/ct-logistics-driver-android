package com.scgexpress.backoffice.android.repository

import com.scgexpress.backoffice.android.api.PickupService
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.model.pickup.PickupTaskAcception
import com.scgexpress.backoffice.android.model.pickup.PickupTaskAcceptionResponse
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Deprecated("replace with repository.pickup.PickupRepository")
class PickupRepository @Inject constructor(private val service: PickupService) {

    val pickupTasks: Flowable<List<PickupTask>>
        get() {
            return service.getPickupTasks().map { it.items }
        }

    val pickupTasksAccept: Single<List<PickupTask>>
        get() {
            return service.getPickupTasksAccept()
        }
    val pickupTasksReject: Single<List<PickupTask>>
        get() {
            return service.getPickupTasksReject()
        }

    fun pickupTask(taskID: String): Single<PickupTask> {
        return service.getPickupTask(taskID)
    }

    fun acceptTask(id: String,body: PickupTaskAcception): Single<PickupTaskAcceptionResponse> {
        return service.taskAcception(id,body)
    }
}
