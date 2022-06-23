package ru.turbopro.testworkapp.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.data.utils.ListTypeConverter

@Database(entities = [ Fruit::class], version = 1)
@TypeConverters(ListTypeConverter::class)
abstract class FruitsAppDatabase : RoomDatabase() {
	abstract fun FruitsDao(): FruitsDao

	companion object {
		@Volatile
		private var INSTANCE: FruitsAppDatabase? = null

		fun getInstance(context: Context): FruitsAppDatabase =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
			}

		private fun buildDatabase(context: Context) =
			Room.databaseBuilder(
				context.applicationContext,
				FruitsAppDatabase::class.java, "FruitsAppDb"
			)
				.fallbackToDestructiveMigration()
				.allowMainThreadQueries()
				.build()
	}
}