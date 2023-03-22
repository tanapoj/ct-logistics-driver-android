package com.scgexpress.backoffice.android.parser.masterdata

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken


class FetchArray : StreamObject {

    val values = mutableListOf<String>()

    constructor(reader: JsonReader) : super(reader) {
        acceptBeginArray()
        while (reader.peek() != JsonToken.END_ARRAY) {
            values.add(parseString(reader))
        }
        acceptEndArray()
    }

    fun getJsonString(): String {
        return makeJsonArrayString(values)
    }

}