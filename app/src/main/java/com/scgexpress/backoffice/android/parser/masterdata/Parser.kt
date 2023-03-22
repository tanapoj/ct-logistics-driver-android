package com.scgexpress.backoffice.android.parser.masterdata

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.lang.NumberFormatException

open abstract class Parser(open val reader: JsonReader) {

    //accept only array or single value
    protected fun parseString(reader: JsonReader): String {
        val current = reader.peek()
        return when (current) {
            JsonToken.END_OBJECT -> throw NotImplementedError()
            JsonToken.BEGIN_ARRAY -> parseArray(reader)
            else -> parseValue(reader)
        }
    }

    protected fun parseArray(reader: JsonReader): String {
        val values = mutableListOf<String>()

        acceptBeginArray()
        while (reader.peek() != JsonToken.END_ARRAY) {
            when (reader.peek()) {
                JsonToken.BEGIN_OBJECT -> FetchObject(reader).getJsonString()
                else -> throw NotImplementedError()
            }.also {
                values.add(it)
            }
        }
        acceptEndArray()

        return makeJsonArrayString(values)
    }

    protected fun parseValue(reader: JsonReader): String {
        testCurrent()
        val value = when (reader.peek()) {
            JsonToken.NUMBER -> {
                try {
                    reader.nextLong()
                } catch (e: NumberFormatException) {
                    reader.nextDouble()
                }
            }
            JsonToken.STRING -> {
                '"' + stringValue(reader.nextString()) + '"'
            }
            JsonToken.BOOLEAN -> {
                reader.nextBoolean()
            }
            JsonToken.NULL -> {
                reader.nextNull()
                return "null"
            }
            JsonToken.BEGIN_OBJECT -> FetchObject(reader).getJsonString()
            else -> throw IllegalStateException()
        }.toString()
        return value
    }

    protected fun parseObject(reader: JsonReader) = FetchObject(reader).getJsonString()

    protected fun acceptBeginObject() {
        testCurrent()
        if (reader.peek() != JsonToken.BEGIN_OBJECT) throw IllegalStateException()
        reader.beginObject()
    }

    protected fun acceptEndObject() {
        testCurrent()
        if (reader.peek() != JsonToken.END_OBJECT) throw IllegalStateException()
        reader.endObject()
    }

    protected fun acceptBeginArray() {
        testCurrent()
        if (reader.peek() != JsonToken.BEGIN_ARRAY) throw IllegalStateException()
        reader.beginArray()
    }

    protected fun acceptEndArray() {
        testCurrent()
        if (reader.peek() != JsonToken.END_ARRAY) throw IllegalStateException()
        reader.endArray()
    }

    protected fun acceptName(): String {
        testCurrent()
        if (reader.peek() != JsonToken.NAME) throw IllegalStateException()
        return reader.nextName()
    }

    protected fun makeJsonObjectString(values: List<Pair<String, String>>): String {
        return "{" + values.map { "\"${stringValue(it.first)}\":${it.second}" }.joinToString(",") + "}"
    }

    protected fun makeJsonArrayString(values: List<String>): String {
        return "[" + values.joinToString(",") + "]"
    }

    protected fun stringValue(str: String) = str.replace("\\", "\\\\").replace("\"", "\\\"")

    protected fun testCurrent() {
        //println(reader.peek())
    }

}