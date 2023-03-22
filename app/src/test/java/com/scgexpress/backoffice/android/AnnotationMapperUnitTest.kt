package com.scgexpress.backoffice.android

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.scgexpress.backoffice.android.common.unserializeTo
import org.junit.Assert.assertEquals
import org.junit.Test

data class A(
    @SerializedName("x") var ant: Int = 0,
    @SerializedName("y") var bird: Double = .0,
    @SerializedName("z") var cat: String = "",
    @SerializedName("w") var dog: Boolean = false
)

data class B(
    @SerializedName("x") var alpha: Int = 0,
    @SerializedName("y") var beta: Double = .0,
    @SerializedName("z") var gamma: String = "",
    @SerializedName("w") var omega: Boolean = false
)


@Entity(tableName = "pickup_task")
data class TestEntity(
    @SerializedName("id") @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id1: Int = -1,
    @SerializedName("title") @ColumnInfo(name = "title") var title: String = "",
    @SerializedName("name") @ColumnInfo(name = "name") var name: String = "",
    @SerializedName("is_done") @ColumnInfo(name = "is_done") var isDone: Boolean = false
)

data class TestModel(
    @SerializedName("id") var id2: Int = -2,
    @SerializedName("title") var title: Double = .0,
    @SerializedName("first_name") var name: String = "",
    @SerializedName("status") var status: String = "",
    var value: Int = 0
)


class AnnotationMapperUnitTest {

    @Test
    fun test_map_entity_to_model() {

        val a1 = A(111, 1.11, "111", true)
        val b1 = B(111, 1.11, "111", true)

        val b2 = a1.unserializeTo(B::class.java)
        val a2 = b2.unserializeTo(A::class.java)
        
        assertEquals(a1, a2)
        assertEquals(b1, b2)

    }

    @Test
    fun test_map_entity_to_model_with_diff() {

        val entity = TestEntity(100, "test entity", "android", true)
        val model = entity.unserializeTo(TestModel::class.java) { serializeName, field, type ->
            //            println("serializeName=$serializeName field=$field")
            if (serializeName != null) {
                when (serializeName) {
                    "title" -> .1234
                    "first_name" -> "this is option name"
                    "status" -> "DONE!"
                    else -> null
                }
            } else {
                when (field) {
                    "value" -> 5000
                    else -> null
                }
            }
        }

        assertEquals(TestModel(100, .1234, "this is option name", "DONE!", 5000), model)

    }


}