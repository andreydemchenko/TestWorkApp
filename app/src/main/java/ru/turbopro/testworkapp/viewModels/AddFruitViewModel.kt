package ru.turbopro.testworkapp.viewModels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.turbopro.testworkapp.ERR_UPLOAD
import ru.turbopro.testworkapp.FruitsApplication
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.data.utils.AddEditFruitErrors
import ru.turbopro.testworkapp.data.utils.StoreFruitDataStatus
import ru.turbopro.testworkapp.ui.AddEditFruitViewErrors
import ru.turbopro.testworkapp.data.Result
import ru.turbopro.testworkapp.getFruitId

private const val TAG = "AddFruitViewModel"

class AddEditFruitViewModel(application: Application) : AndroidViewModel(application) {

    private val FruitsRepository =
        (application.applicationContext as FruitsApplication).fruitsRepository

    private val _fruitId = MutableLiveData<String>()
    val fruitId: LiveData<String> get() = _fruitId

    private val _errorStatus = MutableLiveData<AddEditFruitViewErrors>()
    val errorStatus: LiveData<AddEditFruitViewErrors> get() = _errorStatus

    private val _dataStatus = MutableLiveData<StoreFruitDataStatus>()
    val fruitDataStatus: LiveData<StoreFruitDataStatus> get() = _dataStatus

    private val _addEditFruitErrors = MutableLiveData<AddEditFruitErrors?>()
    val addEditFruitErrors: LiveData<AddEditFruitErrors?> get() = _addEditFruitErrors

    private val _fruitData = MutableLiveData<Fruit>()
    val fruitData: LiveData<Fruit> get() = _fruitData

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val newFruitData = MutableLiveData<Fruit>()

    init {
        _errorStatus.value = AddEditFruitViewErrors.NONE
    }

    fun setFruitData(FruitId: String) {
        _fruitId.value = FruitId
        viewModelScope.launch {
            Log.d(TAG, "onLoad: Getting Fruit Data")
            _dataStatus.value = StoreFruitDataStatus.LOADING
            val res = async { FruitsRepository.getFruitById(FruitId) }
            val evRes = res.await()
            if (evRes is Result.Success) {
                val evData = evRes.data
                _fruitData.value = evData
                Log.d(TAG, "onLoad: Successfully retrieved Fruit data")
                _dataStatus.value = StoreFruitDataStatus.DONE
            } else if (evRes is Result.Error) {
                _dataStatus.value = StoreFruitDataStatus.ERROR
                Log.d(TAG, "onLoad: Error getting Fruit data")
                _fruitData.value = Fruit()
            }
        }
    }

    fun submitFruit(
        name: String,
        calories: String,
        desc: String,
        imgList: List<Uri>,
    ) {
        if (name.isBlank() || calories.isBlank() || desc.isBlank() || imgList.isNullOrEmpty()) {
            _errorStatus.value = AddEditFruitViewErrors.EMPTY
        } else {
            _errorStatus.value = AddEditFruitViewErrors.NONE
            val evId = getFruitId()
            val newFruit =
                Fruit(
                    evId,
                    name.trim(),
                    desc.trim(),
                    calories.trim(),
                    emptyList()
                )
            newFruitData.value = newFruit
            Log.d(TAG, "fruit = $newFruit")
            insertFruit(imgList)
        }
    }

    private fun insertFruit(imgList: List<Uri>) {
        viewModelScope.launch {
            if (newFruitData.value != null) {
                _addEditFruitErrors.value = AddEditFruitErrors.ADDING
                val resImg = async { FruitsRepository.insertImages(imgList) }
                val imagesPaths = resImg.await()
                newFruitData.value?.images = imagesPaths
                if (newFruitData.value?.images?.isNotEmpty() == true) {
                    if (imagesPaths[0] == ERR_UPLOAD) {
                        Log.d(TAG, "error uploading images")
                        _addEditFruitErrors.value = AddEditFruitErrors.ERR_ADD_IMG
                    } else {
                        val deferredRes = async {
                            FruitsRepository.insertFruit(newFruitData.value!!)
                        }
                        val res = deferredRes.await()
                        if (res is Result.Success) {
                            Log.d(TAG, "onInsertFruit: Success")
                            _addEditFruitErrors.value = AddEditFruitErrors.NONE
                        } else {
                            _addEditFruitErrors.value = AddEditFruitErrors.ERR_ADD
                            if (res is Result.Error)
                                Log.d(TAG, "onInsertFruit: Error Occurred, ${res.exception}")
                        }
                    }
                } else {
                    Log.d(TAG, "Fruit images empty, Cannot Add Fruit")
                }
            } else {
                Log.d(TAG, "Fruit is Null, Cannot Add Fruit")
            }
        }
    }
}