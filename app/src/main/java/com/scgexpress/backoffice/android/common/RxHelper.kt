package com.scgexpress.backoffice.android.common

import com.scgexpress.backoffice.android.di.RxThreadScheduler
import io.reactivex.*
import io.reactivex.disposables.Disposable

fun <T> Flowable<T>.toSingle(): Single<T> = Single.create { emitter ->
    subscribe(emitter::onSuccess, emitter::onError)
}

fun <T> Completable.toSingle(value: T): Single<T> = Single.create { emitter ->
    subscribe({
        emitter.onSuccess(value)
    }, emitter::onError)
}

infix fun <T> Completable.thenPass(value: T): Single<T> = toSingle(value)

infix fun <S, T> Single<S>.thenPassWith(attach: T): Single<Pair<S, T>> {
    return Single.create { emitter ->
        subscribe({
            emitter.onSuccess(it to attach)
        }, emitter::onError)
    }
}

fun <T> Completable.thenSingle(singleSource: () -> Single<T>): Single<T> =
    Single.create { emitter ->
        subscribe({
            singleSource().subscribe(emitter::onSuccess, emitter::onError)
        }, emitter::onError)
    }

fun Completable.then(completableSource: () -> Completable): Completable =
    this.andThen(Completable.defer(completableSource))

fun <T> Single<T>.thenCompletable(completableSource: () -> Completable) =
    Completable.defer(completableSource)

//scheduleBy
fun <T> Observable<T>.scheduleBy(scheduler: RxThreadScheduler): Observable<T> =
    subscribeOn(scheduler.io()).observeOn(scheduler.ui())

fun <T> Flowable<T>.scheduleBy(scheduler: RxThreadScheduler): Flowable<T> =
    subscribeOn(scheduler.io()).observeOn(scheduler.ui())

fun <T> Single<T>.scheduleBy(scheduler: RxThreadScheduler): Single<T> =
    subscribeOn(scheduler.io()).observeOn(scheduler.ui())

fun Completable.scheduleBy(scheduler: RxThreadScheduler): Completable =
    subscribeOn(scheduler.io()).observeOn(scheduler.ui())

//auto invoke
operator fun <T> Flowable<T>.invoke(): Disposable = subscribe({}, { it.printStackTrace() }, {})

operator fun <T> Single<T>.invoke(): Disposable = subscribe({}, { it.printStackTrace() })
operator fun Completable.invoke(): Disposable = subscribe({}, { it.printStackTrace() })
//emit with
fun <T> Single<T>.thenEmit(
    emitter: SingleEmitter<T>,
    scheduler: RxThreadScheduler? = null
): Disposable = (scheduler?.run { scheduleBy(scheduler) } ?: this).subscribe(
    emitter::onSuccess,
    emitter::onError
)

fun <T> Single<T>.thenEmit(
    emitter: CompletableEmitter,
    scheduler: RxThreadScheduler? = null
): Disposable = (scheduler?.run { scheduleBy(scheduler) } ?: this).subscribe(
    { emitter.onComplete() },
    emitter::onError
)

fun Completable.thenEmit(
    emitter: CompletableEmitter,
    scheduler: RxThreadScheduler? = null
): Disposable = (scheduler?.run { scheduleBy(scheduler) } ?: this).subscribe(
    emitter::onComplete,
    emitter::onError
)