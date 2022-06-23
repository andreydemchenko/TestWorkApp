package ru.turbopro.testworkapp.data.source.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.data.Result
import ru.turbopro.testworkapp.data.utils.StoreFruitDataStatus

interface FruitsRepoInterface {
    suspend fun refreshFruits(): StoreFruitDataStatus?
    fun observeFruits(): LiveData<Result<List<Fruit>>?>
    suspend fun getFruitById(fruitId: String, forceUpdate: Boolean = false): ru.turbopro.testworkapp.data.Result<Fruit>
    suspend fun insertFruit(newFruit: Fruit): Result<Boolean>
    suspend fun insertImages(imgList: List<Uri>): List<String>
}