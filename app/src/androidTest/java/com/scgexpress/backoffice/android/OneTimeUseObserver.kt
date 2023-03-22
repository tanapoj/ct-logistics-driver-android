package com.scgexpress.backoffice.android

import androidx.lifecycle.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
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

@Deprecated("use future", ReplaceWith("future()"))
fun <T> Observable<T>.waitForResult() = toFuture().get()

@Deprecated("use future", ReplaceWith("future()"))
fun <T> Flowable<T>.waitForResult() = toFuture().get()

@Deprecated("use future", ReplaceWith("future()"))
fun <T> Single<T>.waitForResult() = toFuture().get()

fun Completable.future(): Any? = CompletableFuture<Any?>()
    .also { future ->
        subscribe({
            future.complete(null)
        }, {
            future.complete(null)
        })
    }.get()

fun <T> Flowable<T>.future(): T = CompletableFuture<T>()
    .also { future ->
        subscribe({
            future.complete(it)
        }, {
            future.complete(null)
        })
    }.get()

fun <T> Single<T>.future(): T = CompletableFuture<T>()
    .also { future ->
        subscribe({
            future.complete(it)
        }, {
            future.complete(null)
        })
    }.get()