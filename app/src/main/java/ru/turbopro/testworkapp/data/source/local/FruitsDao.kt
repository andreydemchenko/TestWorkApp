package ru.turbopro.testworkapp.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.turbopro.testworkapp.data.Fruit

@Dao
interface FruitsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(Fruit: Fruit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListOfFruits(Fruits: List<Fruit>)

    @Query("SELECT * FROM fruits")
    suspend fun getAllFruits(): List<Fruit>

    @Query("SELECT * FROM fruits")
    fun observeFruits(): LiveData<List<Fruit>>

    @Query("SELECT * FROM fruits WHERE id = :fruitId")
    suspend fun getFruitById(fruitId: String): Fruit?

    @Query("DELETE FROM fruits")
    suspend fun deleteAllFruits()
    
}