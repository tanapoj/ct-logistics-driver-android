package com.scgexpress.backoffice.android.api

import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.api.annotation.MockResponse
import com.scgexpress.backoffice.android.model.Topic
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TopicService {

    companion object {
        private const val PARAMS_ID: String = "id"
    }

    @GET("posts/{id}")
    //@KeyPathResponse("foo.bar.items")
    //@WrappedResponse(ApiResult::class)
    @MockResponse(R.raw.get_topic)
    fun getTopic(@Query(PARAMS_ID) id: String): Single<Topic>


    @GET("posts")
    //@KeyPathResponse("foo.bar.items")
    //@WrappedResponse(ApiResult::class)
    @MockResponse(R.raw.get_topics)
    fun getTopics(): Single<List<Topic>>
}