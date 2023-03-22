package com.scgexpress.backoffice.android.common

inline fun <T> MutableList<T>.moveToFirst(other: MutableList<T>, predicate: (T) -> Boolean): Boolean {
    if (!any(predicate)) return false
    while (true) {
        val item = firstOrNull(predicate) ?: break
        remove(item)
        other.add(0, item)
    }
    return true
}

inline fun <T> List<T>.minusBy(other: List<T>, predicate: (T) -> Any): List<T> {
    val value = other.map { predicate(it) }
    return filter { predicate(it) !in value }
}

open class PairMovableMutableList<T> {
    var first: MutableList<T> = mutableListOf()
    var second: MutableList<T> = mutableListOf()

    fun get(): Pair<MutableList<T>, MutableList<T>> = first to second

    fun move(predicate: (T) -> Boolean) = first.moveToFirst(second, predicate)

    fun moveBack(predicate: (T) -> Boolean) = second.moveToFirst(first, predicate)

    fun addExtraItem(item: T, index: Int = 0) = second.add(index, item)

    fun <K> distinctItems(selector: (T) -> K) {
        first = first.distinctBy(selector).toMutableList()
        second = second.distinctBy(selector).toMutableList()
    }
}