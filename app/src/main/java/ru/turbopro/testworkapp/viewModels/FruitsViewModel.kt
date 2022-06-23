package ru.turbopro.testworkapp.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.turbopro.testworkapp.FruitsApplication
import ru.turbopro.testworkapp.data.Result
import ru.turbopro.testworkapp.data.utils.StoreFruitDataStatus
import ru.turbopro.testworkapp.data.Fruit

private const val TAG = "FruitsViewModel"

class FruitsViewModel(application: Application) : AndroidViewModel(application) {
    private val fruitsRepository =
        (application.applicationContext as FruitsApplication).fruitsRepository

    private var _fruits = MutableLiveData<List<Fruit>>()
    val fruits: LiveData<List<Fruit>> get() = _fruits

    private lateinit var _allFruits: MutableLiveData<List<Fruit>>
    val allFruits: LiveData<List<Fruit>> get() = _allFruits

    private val _storeDataStatus = MutableLiveData<StoreFruitDataStatus>()
    val storeFruitDataStatus: LiveData<StoreFruitDataStatus> get() = _storeDataStatus

    private val _dataStatus = MutableLiveData<StoreFruitDataStatus>()
    val fruitDataStatus: LiveData<StoreFruitDataStatus> get() = _dataStatus

    init {
        getFruits()
    }

    fun setDataLoaded() {
        _storeDataStatus.value = StoreFruitDataStatus.DONE
    }

    private fun getFruits() {
        _allFruits = Transformations.switchMap(fruitsRepository.observeFruits()) {
            getFruitsLiveData(it)
        } as MutableLiveData<List<Fruit>>
        viewModelScope.launch {
            _storeDataStatus.value = StoreFruitDataStatus.LOADING
            val res = async { fruitsRepository.refreshFruits() }
            res.await()
            Log.d(TAG, "getAllFruits: status = ${_storeDataStatus.value}")
        }
        _fruits.value = _allFruits.value
    }

    private fun getFruitsLiveData(result: Result<List<Fruit>?>?): LiveData<List<Fruit>> {
        val res = MutableLiveData<List<Fruit>>()
        if (result is Result.Success) {
            Log.d(TAG, "result is success")
            _storeDataStatus.value = StoreFruitDataStatus.DONE
            res.value = result.data ?: emptyList()
        } else {
            Log.d(TAG, "result is not success")
            res.value = emptyList()
            _storeDataStatus.value = StoreFruitDataStatus.ERROR
            if (result is Result.Error)
                Log.d(TAG, "getFruitsLiveData: Error Occurred: ${result.exception}")
        }
        return res
    }

    fun refreshFruits() {
        getFruits()
    }
}