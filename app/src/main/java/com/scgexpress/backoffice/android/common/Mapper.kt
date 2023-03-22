package com.scgexpress.backoffice.android.common

import com.google.gson.annotations.SerializedName
import java.lang.Exception


fun <A : Any, B> A.unserializeTo(
    target: Class<out B>,
    otherCase: ((annotation: String?, field: String, type: Class<*>) -> Any?)? = null
): B {

    val declaredFields = this::class.java.declaredFields
    val map = mutableMapOf<String, Any?>()

    for (field in declaredFields) {
        for (annotation in field.annotations) {
            when (annotation) {
                is SerializedName -> {
                    field.isAccessible = true
                    map[annotation.value] = field.get(this)
                }
            }
        }
    }

    val newModel: B = try {
        target.newInstance()
    } catch (ex: Exception) {
        throw IllegalArgumentException("class '$target' must has empty constructor (able to new object without parameter passing)")
    }

    val declaredAttrs = target.declaredFields
    fieldsLoop@ for (field in declaredAttrs) {
        var serializeName: String? = null
        for (annotation in field.annotations) {
            if (annotation is SerializedName) {
                serializeName = annotation.value
                try {
                    if (map.containsKey(annotation.value)) {
                        field.isAccessible = true
                        field.set(newModel, map[annotation.value])
                        continue@fieldsLoop
                    }

                } catch (ex: IllegalArgumentException) {
                }
            } else {
                continue
            }

        }
        otherCase?.let { mapper ->
            try {
                mapper(serializeName, field.name, field.type)?.let { value ->
                    field.isAccessible = true
                    field.set(newModel, value)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    return newModel
}