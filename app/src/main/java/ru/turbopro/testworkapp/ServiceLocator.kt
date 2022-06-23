package ru.turbopro.testworkapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import ru.turbopro.testworkapp.data.source.FruitDataSource
import ru.turbopro.testworkapp.data.source.local.FruitsAppDatabase
import ru.turbopro.testworkapp.data.source.local.FruitsLocalDataSource
import ru.turbopro.testworkapp.data.source.remote.FruitRemoteDataSource
import ru.turbopro.testworkapp.data.source.repository.FruitsRepoInterface
import ru.turbopro.testworkapp.data.source.repository.FruitsRepository

object ServiceLocator {
	private var database: FruitsAppDatabase? = null
	private val lock = Any()

	@Volatile
	var FruitsRepository: FruitsRepoInterface? = null
		@VisibleForTesting set

	fun provideFruitsRepository(context: Context): FruitsRepoInterface {
		synchronized(this) {
			return FruitsRepository ?: createFruitsRepository(context)
		}
	}

	private fun createFruitsRepository(context: Context): FruitsRepoInterface {
		val newRepo =
			FruitsRepository(FruitRemoteDataSource(), createFruitsLocalDataSource(context))
		FruitsRepository = newRepo
		return newRepo
	}

	private fun createFruitsLocalDataSource(context: Context): FruitDataSource {
		val database = database ?: FruitsAppDatabase.getInstance(context.applicationContext)
		return FruitsLocalDataSource(database.FruitsDao())
	}
}