package com.scgexpress.backoffice.android

import androidx.lifecycle.*
import java.util.concurrent.CompletableFuture

class OneTimeUseObserver<T>(private val onChange: (T) -> Unit) : Observer<T>, LifecycleOwner {

    private val lifecycle = LifecycleRegistry(this)

    init {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun onChanged(t: T) {
        onChange(t)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    fun getLifecycleOwner(): LifecycleOwner = this
}

fun <T> LiveData<T>.observeOnce(onDataChanged: (T) -> Unit) {
    val observer = OneTimeUseObserver(onChange = onDataChanged)
    observe(observer.getLifecycleOwner(), observer)
}

inline fun <T> LiveData<T>.future(): T = CompletableFuture<T>()
    .also { future ->
        observeOnce {
            future.complete(it)
        }
    }.get()