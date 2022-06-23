package ru.turbopro.testworkapp.data.source

import android.net.Uri
import androidx.lifecycle.LiveData
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.data.Result

interface FruitDataSource {

    fun observeFruits(): LiveData<Result<List<Fruit>>?>

    suspend fun getAllFruits(): Result<List<Fruit>>

    suspend fun refreshFruits() {}

    suspend fun getFruitById(FruitId: String): Result<Fruit>

    suspend fun insertFruit(newFruit: Fruit)

    suspend fun uploadImage(uri: Uri, fileName: String): Uri? {
        return null
    }

    fun revertUpload(fileName: String) {}

    suspend fun deleteAllFruits() {}
    suspend fun insertMultipleFruits(data: List<Fruit>) {}
}