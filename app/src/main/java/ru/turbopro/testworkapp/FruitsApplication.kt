package ru.turbopro.testworkapp

import android.app.Application
import ru.turbopro.testworkapp.data.source.repository.FruitsRepoInterface

class FruitsApplication : Application() {

	val fruitsRepository: FruitsRepoInterface
		get() = ServiceLocator.provideFruitsRepository(this)

	override fun onCreate() {
		super.onCreate()
	}
}