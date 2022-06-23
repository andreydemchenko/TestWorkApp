package ru.turbopro.testworkapp.data.source.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.data.source.FruitDataSource
import ru.turbopro.testworkapp.data.Result

class FruitRemoteDataSource : FruitDataSource {

    private val firebaseDb: FirebaseFirestore = Firebase.firestore
    private val firebaseStorage: FirebaseStorage = Firebase.storage

    private val observableFruits = MutableLiveData<Result<List<Fruit>>?>()

    private fun storageRef() = firebaseStorage.reference
    private fun fruitsCollectionRef() = firebaseDb.collection(FRUIT_COLLECTION)

    override suspend fun refreshFruits() {
        observableFruits.value = getAllFruits()
    }

    override fun observeFruits(): LiveData<Result<List<Fruit>>?> {
        return observableFruits
    }

    override suspend fun getAllFruits(): Result<List<Fruit>> {
        val resRef = fruitsCollectionRef().get().await()
        return if (!resRef.isEmpty) {
            Result.Success(resRef.toObjects(Fruit::class.java))
        } else {
            Result.Error(Exception("Error getting Fruits!"))
        }
    }

    override suspend fun insertFruit(newFruit: Fruit) {
        fruitsCollectionRef().add(newFruit.toHashMap()).await()
    }

    override suspend fun getFruitById(fruitId: String): Result<Fruit> {
        val resRef = fruitsCollectionRef().whereEqualTo(FRUIT_ID_FIELD, fruitId).get().await()
        return if (!resRef.isEmpty) {
            Result.Success(resRef.toObjects(Fruit::class.java)[0])
        } else {
            Result.Error(Exception("Fruit with id: $fruitId Not Found!"))
        }
    }

    override suspend fun uploadImage(uri: Uri, fileName: String): Uri? {
        val imgRef = storageRef().child("$FRUITS_STORAGE_PATH/$fileName")
        val uploadTask = imgRef.putFile(uri)
        val uriRef = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imgRef.downloadUrl
        }
        return uriRef.await()
    }

    override fun revertUpload(fileName: String) {
        val imgRef = storageRef().child("${FRUITS_STORAGE_PATH}/$fileName")
        imgRef.delete().addOnSuccessListener {
            Log.d(TAG, "onRevert: File with name: $fileName deleted successfully!")
        }.addOnFailureListener { e ->
            Log.d(TAG, "onRevert: Error deleting file with name = $fileName, error: $e")
        }
    }

    companion object {
        private const val FRUIT_COLLECTION = "products"
        private const val FRUIT_ID_FIELD = "id"
        private const val FRUITS_STORAGE_PATH = "photos"
        private const val TAG = "FruitsRemoteSource"
    }
}