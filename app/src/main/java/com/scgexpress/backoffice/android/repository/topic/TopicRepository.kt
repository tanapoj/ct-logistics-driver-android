package com.scgexpress.backoffice.android.repository.topic

import com.google.common.collect.Lists
import com.scgexpress.backoffice.android.api.TopicService
import com.scgexpress.backoffice.android.db.dao.TopicDao
import com.scgexpress.backoffice.android.db.entity.TopicEntity
import com.scgexpress.backoffice.android.model.Topic
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicRepository @Inject constructor(private val service: TopicService, private val dao: TopicDao) {

    val topics: Single<List<Topic>>
        get() {
            return service.getTopics()
        }

    val topicsLocal: Flowable<List<Topic>>
        get() {
            return dao.all.map(this::pareTopicList)
        }



    fun getTopic(id: String): Single<Topic> {
        return service.getTopic(id)
    }

    fun saveTopic(item: Topic): Long {
        val itemEntity =
            TopicEntity(
                id = item.id,
                userId = item.userId,
                title = item.title,
                body = item.body
            )
        return dao.insertOrReplace(itemEntity)
    }

    fun saveTopic(items: List<Topic>): List<Long> {
        val itemList = Lists.transform(
            items
        ) { input ->
            TopicEntity(
                id = input!!.id,
                userId = input.userId,
                title = input.title,
                body = input.body
            )
        }
        return dao.insertOrReplace(itemList)
    }

    fun deleteTopics(items: List<Topic>): Single<Int> {
        val itemList = Lists.transform(
            items
        ) { input ->
            TopicEntity(
                id = input!!.id,
                userId = input.userId,
                title = input.title,
                body = input.body
            )
        }
        return dao.delete(itemList)
    }

    fun deleteTopic(item: Topic): Single<Int> {
        val entity = TopicEntity(
            id = item.id,
            userId = item.userId,
            title = item.title,
            body = item.body
        )
        return dao.delete(entity)
    }

    fun deleteTopic(id: String, userId: String): Single<Int> {
        return dao.delete(id,userId)
    }

    private fun pareTopicList(entityList: List<TopicEntity>?): List<Topic>? {
        if (entityList != null) {
            val modelList: ArrayList<Topic> = arrayListOf()
            for (entity in entityList) {
                modelList.add(Topic(entity))
            }
            return modelList
        }
        return null
    }
}
