package com.scgexpress.backoffice.android.parser.masterdata

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken


open class StreamObject(override val reader: JsonReader) : Parser(reader) {

    open fun hasNext(): Boolean = reader.peek() != JsonToken.END_OBJECT && reader.peek() != JsonToken.END_DOCUMENT

    open fun access() {
        if (!isObject()) throw IllegalStateException()
        acceptBeginObject()
    }

    open fun exit() {
        acceptEndObject()
    }

    open fun nextObject(): ObjectInfoHolder {

        val key = acceptName()
        val value = FetchArray(reader).getJsonString()

        return ObjectInfoHolder(key, value, makeJsonObjectString(listOf(Pair(key, value))))
    }

    fun nextStreamArray(): StreamArray {
        return StreamArray(reader)
    }

    protected fun isObject() = reader.peek() == JsonToken.BEGIN_OBJECT
    protected fun isArray() = reader.peek() == JsonToken.BEGIN_ARRAY

    data class ObjectInfoHolder(val key: String, val value: String, val json: String)
    data class ArrayInfoHolder(val index: Int, val parentKey: String, val value: String, val json: String)

}