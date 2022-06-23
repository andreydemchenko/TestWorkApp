package ru.turbopro.testworkapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.HashMap

@Parcelize
@Entity(tableName = "fruits")
data class Fruit @JvmOverloads constructor(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var calories: String = "",
    var images: List<String> = ArrayList(),
) : Parcelable {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "calories" to calories,
            "images" to images
        )
    }
}