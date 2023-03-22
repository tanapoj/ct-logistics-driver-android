package com.scgexpress.backoffice.android.parser.masterdata

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken


class StreamArray(reader: JsonReader) : StreamObject(reader) {

    var parentKey: String = ""
    val values = mutableListOf<String>()
    var idx = 0

    override fun access() {
        if (isObject()) {
            acceptBeginObject()
        }

        parentKey = acceptName()

        if (!isArray()) throw IllegalStateException()
        acceptBeginArray()
    }

    override fun exit() {
        acceptEndArray()
    }

    override fun hasNext() = reader.peek() != JsonToken.END_ARRAY

    fun nextPartialObject(): ArrayInfoHolder? {

        if (reader.peek() == JsonToken.END_ARRAY) {
            acceptEndArray()
            return null
        }

        val value = FetchObject(reader).getJsonString()

        return ArrayInfoHolder(idx++, parentKey, value, makeJsonObjectString(listOf(Pair(parentKey, "[$value]"))))
    }

    fun nextPartialObject(rows: Int = 1): ArrayInfoHolder? {

        if(rows <= 1){
            return nextPartialObject()
        }

        val index = idx

        var i = 0
        val values = mutableListOf<String>()
        while(i < rows && reader.peek() != JsonToken.END_ARRAY){
            FetchObject(reader).getJsonString().also { values.add(it) }
            i++
            idx++
        }

        val value = makeJsonArrayString(values)

        return ArrayInfoHolder(index, parentKey, value, makeJsonObjectString(listOf(Pair(parentKey, value))))
    }

}