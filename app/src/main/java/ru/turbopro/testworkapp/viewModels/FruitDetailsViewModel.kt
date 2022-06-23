package ru.turbopro.testworkapp.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.turbopro.testworkapp.FruitsApplication
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.data.Result
import ru.turbopro.testworkapp.data.utils.AddFruitObjectStatus
import ru.turbopro.testworkapp.data.utils.StoreFruitDataStatus
import ru.turbopro.testworkapp.ui.AddFruitItemErrors

private const val TAG = "FruitViewModel"

class FruitDetailsViewModel(private val fruitId: String, application: Application) :
    AndroidViewModel(application) {

    private val _fruitData = MutableLiveData<Fruit?>()
    val fruitData: LiveData<Fruit?> get() = _fruitData

    private var _fruits = MutableLiveData<List<Fruit>>()
    val fruits: LiveData<List<Fruit>> get() = _fruits

    private val _dataStatus = MutableLiveData<StoreFruitDataStatus>()
    val fruitDataStatus: LiveData<StoreFruitDataStatus> get() = _dataStatus

    private val _errorStatus = MutableLiveData<List<AddFruitItemErrors>>()
    val errorStatus: LiveData<List<AddFruitItemErrors>> get() = _errorStatus

    private val _addItemStatus = MutableLiveData<AddFruitObjectStatus?>()
    val addFruitItemStatus: LiveData<AddFruitObjectStatus?> get() = _addItemStatus

    private val fruitsRepository = (application as FruitsApplication).fruitsRepository

    init {
        _errorStatus.value = emptyList()
        viewModelScope.launch {
            Log.d(TAG, "init: fruitId: $fruitId")
            getFruitDetails()
        }
    }

    fun getCountOfFruitsList() = _fruitData.value!!.images.count()

    private fun getFruitDetails() {
        viewModelScope.launch {
            _dataStatus.value = StoreFruitDataStatus.LOADING
            try {
                Log.d(TAG, "getting Fruit Data")
                val res = fruitsRepository.getFruitById(fruitId)
                if (res is Result.Success) {
                    _fruitData.value = res.data
                    _dataStatus.value = StoreFruitDataStatus.DONE
                } else if (res is Result.Error) {
                    throw Exception("Error getting Fruit")
                }
            } catch (e: Exception) {
                _fruitData.value = Fruit()
                _dataStatus.value = StoreFruitDataStatus.ERROR
            }
        }
    }
}