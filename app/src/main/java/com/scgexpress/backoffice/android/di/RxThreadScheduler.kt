package com.scgexpress.backoffice.android.di

import io.reactivex.Scheduler

interface RxThreadScheduler {
    fun computation(): Scheduler
    fun io(): Scheduler
    fun ui(): Scheduler
}
