package ru.turbopro.testworkapp.data.source.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import ru.turbopro.testworkapp.ERR_UPLOAD
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.data.source.FruitDataSource
import ru.turbopro.testworkapp.data.utils.StoreFruitDataStatus
import ru.turbopro.testworkapp.data.Result
import java.util.*

class FruitsRepository(
    private val fruitsRemoteSource: FruitDataSource,
    private val fruitsLocalSource: FruitDataSource
) : FruitsRepoInterface{

    companion object {
        private const val TAG = "FruitsRepository"
    }

    override suspend fun refreshFruits(): StoreFruitDataStatus? {
        Log.d(TAG, "Updating Fruits in Room")
        return updateFruitsFromRemoteSource()
    }

    override fun observeFruits(): LiveData<Result<List<Fruit>>?> {
        return fruitsLocalSource.observeFruits()
    }

    override suspend fun getFruitById(fruitId: String, forceUpdate: Boolean): Result<Fruit> {
        if (forceUpdate) {
            updateFruitFromRemoteSource(fruitId)
        }
        return fruitsLocalSource.getFruitById(fruitId)
    }

    override suspend fun insertFruit(newFruit: Fruit): Result<Boolean> {
        return supervisorScope {
            val localRes = async {
                Log.d(TAG, "onInsertFruit: adding Fruit to local source")
                fruitsLocalSource.insertFruit(newFruit)
            }
            val remoteRes = async {
                Log.d(TAG, "onInsertFruit: adding Fruit to remote source")
                fruitsRemoteSource.insertFruit(newFruit)
            }
            try {
                localRes.await()
                remoteRes.await()
                Result.Success(true)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun insertImages(imgList: List<Uri>): List<String> {
        var urlList = mutableListOf<String>()
        imgList.forEach label@{ uri ->
            val uniId = UUID.randomUUID().toString()
            val fileName = uniId + uri.lastPathSegment?.split("/")?.last()
            try {
                val downloadUrl = fruitsRemoteSource.uploadImage(uri, fileName)
                urlList.add(downloadUrl.toString())
            } catch (e: Exception) {
                fruitsRemoteSource.revertUpload(fileName)
                Log.d(TAG, "exception: message = $e")
                urlList = mutableListOf()
                urlList.add(ERR_UPLOAD)
                return@label
            }
        }
        return urlList
    }

    private suspend fun updateFruitsFromRemoteSource(): StoreFruitDataStatus? {
        var res: StoreFruitDataStatus? = null
        try {
            val remoteFruits = fruitsRemoteSource.getAllFruits()
            if (remoteFruits is Result.Success) {
                Log.d(TAG, "pro list = ${remoteFruits.data}")
                fruitsLocalSource.deleteAllFruits()
                fruitsLocalSource.insertMultipleFruits(remoteFruits.data)
                res = StoreFruitDataStatus.DONE
            } else {
                res = StoreFruitDataStatus.ERROR
                if (remoteFruits is Result.Error)
                    throw remoteFruits.exception
            }
        } catch (e: Exception) {
            Log.d(TAG, "onUpdateFruitsFromRemoteSource: Exception occurred, ${e.message}")
        }

        return res
    }

    private suspend fun updateFruitFromRemoteSource(FruitId: String): StoreFruitDataStatus? {
        var res: StoreFruitDataStatus? = null
        try {
            val remoteFruit = fruitsRemoteSource.getFruitById(FruitId)
            if (remoteFruit is Result.Success) {
                fruitsLocalSource.insertFruit(remoteFruit.data)
                res = StoreFruitDataStatus.DONE
            } else {
                res = StoreFruitDataStatus.ERROR
                if (remoteFruit is Result.Error)
                    throw remoteFruit.exception
            }
        } catch (e: Exception) {
            Log.d(TAG, "onUpdateFruitFromRemoteSource: Exception occurred, ${e.message}")
        }
        return res
    }
}