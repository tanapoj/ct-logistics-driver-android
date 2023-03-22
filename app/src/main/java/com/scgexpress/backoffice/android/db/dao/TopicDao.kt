package com.scgexpress.backoffice.android.db.dao

import androidx.room.*
import com.scgexpress.backoffice.android.db.entity.TopicEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface TopicDao {

    /* Get */
    @get:Query("SELECT * FROM topic ORDER BY id ASC")
    val all: Flowable<List<TopicEntity>>

    @get:Query("SELECT * FROM topic WHERE userId != '-1' ORDER BY id DESC LIMIT 1")
    val lastRow: Single<TopicEntity>

    @Query("SELECT * FROM topic WHERE userId = :userId AND id = :id")
    fun getBy(userId: String, id: String): Flowable<List<TopicEntity>>

    /* Insert or replace */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entityList: List<TopicEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: TopicEntity): Long

    /* Delete */
    @Delete
    fun delete(entity: TopicEntity): Single<Int>

    @Delete
    fun delete(entityList: List<TopicEntity>): Single<Int>

    @Query("DELETE FROM topic WHERE userId = :userId AND id = :id")
    fun delete(userId: String, id: String): Single<Int>
}