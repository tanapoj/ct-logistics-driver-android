package com.scgexpress.backoffice.android.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.db.AppDatabase
import com.scgexpress.backoffice.android.db.dao.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseModule.Declarations::class])
internal class DatabaseModule {
    @Module
    internal interface Declarations {
        @Binds
        @Singleton
        fun bindRoomDatabase(appDatabase: AppDatabase): RoomDatabase
    }

    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, Const.ROOM_DATABASE_NAME)
            //.addMigrations()
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideDeliveryDao(appDatabase: AppDatabase): DeliveryDao {
        return appDatabase.deliveryDao()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }

    @Provides
    @Singleton
    fun provideMasterDataDao(appDatabase: AppDatabase): MasterDataDao {
        return appDatabase.masterDataDao()
    }

    @Provides
    @Singleton
    fun providePickupDao(appDatabase: AppDatabase): PickupDao {
        return appDatabase.pickupDao()
    }

    @Provides
    @Singleton
    fun provideTrackingPositionDao(appDatabase: AppDatabase): TrackingPositionDao {
        return appDatabase.trackingPositionDao()
    }

    @Provides
    @Singleton
    fun provideTopicDao(appDatabase: AppDatabase): TopicDao {
        return appDatabase.topicDao()
    }
}