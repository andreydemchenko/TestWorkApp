package ru.turbopro.testworkapp.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.data.Result
import ru.turbopro.testworkapp.data.source.FruitDataSource

class FruitsLocalDataSource internal constructor(
    private val fruitsDao: FruitsDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FruitDataSource {

    override fun observeFruits(): LiveData<Result<List<Fruit>>?> {
        return try {
            Transformations.map(fruitsDao.observeFruits()) {
                Result.Success (it)
            }
        } catch (e: Exception) {
            Transformations.map(MutableLiveData(e)) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getAllFruits(): Result<List<Fruit>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(fruitsDao.getAllFruits())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getFruitById(fruitId: String): Result<Fruit> =
        withContext(ioDispatcher) {
            try {
                val fruit = fruitsDao.getFruitById(fruitId)
                if (fruit != null) {
                    return@withContext Result.Success(fruit)
                } else {
                    return@withContext Result.Error(Exception("Fruit Not Found!"))
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun insertFruit(newFruit: Fruit) = withContext(ioDispatcher) {
        fruitsDao.insert(newFruit)
    }

    override suspend fun insertMultipleFruits(data: List<Fruit>) = withContext(ioDispatcher) {
        fruitsDao.insertListOfFruits(data)
    }

    override suspend fun deleteAllFruits() = withContext(ioDispatcher) {
        fruitsDao.deleteAllFruits()
    }
}