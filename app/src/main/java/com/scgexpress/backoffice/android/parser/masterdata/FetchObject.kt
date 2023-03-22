package com.scgexpress.backoffice.android.parser.masterdata

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken


class FetchObject : StreamObject {

    val values = mutableListOf<Pair<String, String>>()

    constructor(reader: JsonReader) : super(reader) {
        acceptBeginObject()
        while (reader.peek() != JsonToken.END_OBJECT) {
            val key = acceptName()
            val valueString = parseString(reader)
            values.add(Pair(key, valueString))
        }
        acceptEndObject()
    }

    fun getJsonString(): String {
        return makeJsonObjectString(values)
    }

}