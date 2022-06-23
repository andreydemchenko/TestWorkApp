package ru.turbopro.testworkapp.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import ru.turbopro.testworkapp.R
import ru.turbopro.testworkapp.data.utils.AddEditFruitErrors
import ru.turbopro.testworkapp.data.utils.StoreFruitDataStatus
import ru.turbopro.testworkapp.databinding.ActivityAddFruitBinding
import ru.turbopro.testworkapp.viewModels.AddEditFruitViewModel
import kotlin.properties.Delegates

class AddFruitActivity : AppCompatActivity() {

    private val TAG = AddFruitActivity::class.java.simpleName

    private lateinit var binding: ActivityAddFruitBinding
    private lateinit var viewModel: AddEditFruitViewModel
    private val focusChangeListener = MyOnFocusChangeListener()

    private var imgList = mutableListOf<Uri>()

    private val getImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { result ->
            imgList.addAll(result)
            if (imgList.size > 3) {
                imgList = imgList.subList(0, 3)
                makeToast("Максимум 3 изображения можно загрузить!")
            }
            val adapter = AddImagesAdapter(this, imgList)
            binding.addFruitImagesRv.adapter = adapter
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddFruitBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(AddEditFruitViewModel::class.java)

        setViews()

        setObservers()

        setContentView(binding.root)
    }

    private fun setObservers() {
        viewModel.errorStatus.observe(this) { err ->
            modifyErrors(err)
        }
        viewModel.fruitDataStatus.observe(this) { status ->
            when (status) {
                StoreFruitDataStatus.LOADING -> {
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                    binding.loaderLayout.circularLoader.showAnimationBehavior
                }
                StoreFruitDataStatus.DONE -> {
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                    binding.loaderLayout.circularLoader.hideAnimationBehavior
                }
                else -> {
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                    binding.loaderLayout.circularLoader.hideAnimationBehavior
                    makeToast("Error getting Data, Try Again!")
                }
            }
        }
        viewModel.addEditFruitErrors.observe(this) { status ->
            when (status) {
                AddEditFruitErrors.ADDING -> {
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                    binding.loaderLayout.circularLoader.showAnimationBehavior
                }
                AddEditFruitErrors.ERR_ADD_IMG -> {
                    setAddFruitErrors("Ошибка добавления изображений, попробуйте ещё раз!")
                }
                AddEditFruitErrors.ERR_ADD -> {
                    setAddFruitErrors("Ошибка добавления фрукта!")
                }
                AddEditFruitErrors.NONE -> {
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                    binding.loaderLayout.circularLoader.hideAnimationBehavior
                }
                else -> {}
            }
        }
    }

    private fun setAddFruitErrors(errText: String) {
        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
        binding.loaderLayout.circularLoader.hideAnimationBehavior
        binding.addFruitErrorTextView.visibility = View.VISIBLE
        binding.addFruitErrorTextView.text = errText
    }

    private fun setViews() {
        Log.d(TAG, "set views")

        val adapter = AddImagesAdapter(this, imgList)
        binding.addFruitImagesRv.adapter = adapter
        binding.addFruitImagesBtn.setOnClickListener {
            getImages.launch("image/*")
        }

        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE

        binding.addFruitErrorTextView.visibility = View.GONE
        binding.addFruitNameEditText.onFocusChangeListener = focusChangeListener
        binding.addFruitCaloriesEditText.onFocusChangeListener = focusChangeListener
        binding.addFruitDescriptionEditText.onFocusChangeListener = focusChangeListener

        binding.addFruitBtn.setOnClickListener {
            onAddFruit()
            if (viewModel.errorStatus.value == AddEditFruitViewErrors.NONE) {
                viewModel.addEditFruitErrors.observe(this) { err ->
                    if (err == AddEditFruitErrors.NONE) {
                        val intent = Intent(this@AddFruitActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun onAddFruit() {
        val name = binding.addFruitNameEditText.text.toString()
        val calories = binding.addFruitCaloriesEditText.text.toString()
        val desc = binding.addFruitDescriptionEditText.text.toString()
        Log.d(TAG, "onAddFruit: Add Fruit initiated, $name, $calories, $desc, $imgList")
        viewModel.submitFruit(
            name, calories, desc, imgList
        )
    }

    private fun modifyErrors(err: AddEditFruitViewErrors) {
        when (err) {
            AddEditFruitViewErrors.NONE -> binding.addFruitErrorTextView.visibility = View.GONE
            AddEditFruitViewErrors.EMPTY -> {
                binding.addFruitErrorTextView.visibility = View.VISIBLE
                binding.addFruitErrorTextView.text = "Заполните все поля!"
            }
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}