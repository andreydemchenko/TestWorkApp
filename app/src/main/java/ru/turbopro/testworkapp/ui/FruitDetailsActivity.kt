package ru.turbopro.testworkapp.ui

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.PagerSnapHelper
import ru.turbopro.testworkapp.R
import ru.turbopro.testworkapp.data.utils.StoreFruitDataStatus
import ru.turbopro.testworkapp.databinding.ActivityFruitDetailsBinding
import ru.turbopro.testworkapp.viewModels.FruitDetailsViewModel

class FruitDetailsActivity : AppCompatActivity() {

    inner class FruitViewModelFactory(
        private val fruitId: String,
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FruitDetailsViewModel::class.java)) {
                return FruitDetailsViewModel(fruitId, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }

    private lateinit var binding: ActivityFruitDetailsBinding
    private lateinit var viewModel: FruitDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFruitDetailsBinding.inflate(layoutInflater)

        val intent = intent
        val fruitId = intent.getStringExtra("fruitId")

        if (fruitId != null) {
            val viewModelFactory = FruitViewModelFactory(fruitId, application)
            viewModel = ViewModelProvider(this, viewModelFactory).get(FruitDetailsViewModel::class.java)
        }

        binding.loaderLayout.loaderFrameLayout.background =
            ResourcesCompat.getDrawable(resources, R.color.white, null)

        setObservers()

        setContentView(binding.root)
    }

    private fun setObservers() {
        viewModel.fruitDataStatus.observe(this) {
            when (it) {
                StoreFruitDataStatus.DONE -> {
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                    binding.fruitDetailsLayout.visibility = View.VISIBLE
                    setViews()
                }
                else -> {
                    binding.fruitDetailsLayout.visibility = View.GONE
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setViews() {
        binding.fruitDetailsLayout.visibility = View.VISIBLE

        setImagesView()

        supportActionBar?.title = viewModel.fruitData.value?.name ?: "Фрукт"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.fruitDetailsNameTv.text = viewModel.fruitData.value?.name ?: ""
        binding.fruitCaloriesTv.text = viewModel.fruitData.value?.calories.toString() + " ккал"

        binding.fruitDetailsDescriptionTv.text = viewModel.fruitData.value?.description ?: ""
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun setImagesView() {
        binding.fruitDetailsImagesRecyclerview.isNestedScrollingEnabled = false
        val adapter = ImagesAdapter(
            this,
            viewModel.fruitData.value?.images ?: emptyList()
        )
        binding.fruitDetailsImagesRecyclerview.adapter = adapter
        val rad = resources.getDimension(R.dimen.radius)
        val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height)
        val inactiveColor = ContextCompat.getColor(this, R.color.gray)
        val activeColor = ContextCompat.getColor(this, R.color.teal_200)
        val itemDecoration =
            DotsIndicatorDecoration(rad, rad * 4, dotsHeight, inactiveColor, activeColor)
        if (viewModel.getCountOfFruitsList() > 1) binding.fruitDetailsImagesRecyclerview.addItemDecoration(
            itemDecoration)
        PagerSnapHelper().attachToRecyclerView(binding.fruitDetailsImagesRecyclerview)
    }
}