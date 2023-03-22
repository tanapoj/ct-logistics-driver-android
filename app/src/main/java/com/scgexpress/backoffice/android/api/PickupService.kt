package com.scgexpress.backoffice.android.api

import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.annotation.MockResponse
import com.scgexpress.backoffice.android.model.pickup.*
import com.scgexpress.backoffice.android.model.tracking.Tracking
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.*

interface PickupService {

    companion object {
        private const val PARAMS_TRACKING_ID: String = "tracking_id"
        private const val PARAMS_TASK_ID: String = "task_id"
        private const val PARAMS_RECEIPT_ID: String = "receipt_id"
    }

//    @GET("pickup/tasks/{$PARAMS_TASK_ID}")
//    @KeyPathResponse("foo.bar.items")
//    @WrappedResponse(ApiResult::class)
//    @MockResponse(R.raw.example)
//    fun getExample(@Path(PARAMS_TASK_ID) id: String): Single<Res>

    @GET("pickup/tasks/{$PARAMS_TASK_ID}")
    @MockResponse(R.raw.get_pickup_task_completed)
    fun getPickupTask(@Path(PARAMS_TASK_ID) id: String): Single<PickupTask>

    @GET("pickup/tasks/{$PARAMS_TASK_ID}")
    @MockResponse(R.raw.get_pickup_task_completed)
    fun getPickupTaskCompleted(@Path(PARAMS_TASK_ID) id: String): Single<PickupTask>

    @GET("pickup/tasks")
    @MockResponse(R.raw.get_pickup_tasks_2)
    fun getPickupTasks(@Query("withs") withs: String = "offline_data"): Flowable<PickupTaskList>

    @POST("pickup/tasks/{$PARAMS_TASK_ID}/acception")
    @MockResponse(R.raw.pickup_task_acception)
    fun taskAcception(@Path(PARAMS_TASK_ID) id: String, @Body body: PickupTaskAcception): Single<PickupTaskAcceptionResponse>

    @GET("pickup/tasks")
    @MockResponse(R.raw.get_pickup_tasks_accept)
    fun getPickupTasksAccept(): Single<List<PickupTask>>

    @GET("pickup/tasks")
    @MockResponse(R.raw.get_pickup_tasks_reject)
    fun getPickupTasksReject(): Single<List<PickupTask>>

    @GET("trackings/{$PARAMS_TRACKING_ID}")
    @MockResponse(R.raw.get_tracking)
    fun getTracking(@Path(PARAMS_TRACKING_ID) trackingId: String): Single<Tracking>

    @POST("pickup/tasks/{$PARAMS_TASK_ID}/summary")
    @MockResponse(R.raw.response_submit_receipt)
    fun submitReceipt(@Path(PARAMS_TASK_ID) taskId: String, @Body body: SubmitReceipt): Single<SubmitReceiptResponse>

    @POST("receipts/{$PARAMS_RECEIPT_ID}")
    @MockResponse(R.raw.response_resend_receipt)
    fun resendReceipt(@Path(PARAMS_RECEIPT_ID) receiptId: String, @Body body: ResendReceipt): Single<ResendReceiptResponse>
}