package ru.turbopro.testworkapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.data.utils.StoreFruitDataStatus
import ru.turbopro.testworkapp.databinding.ActivityMainBinding
import ru.turbopro.testworkapp.viewModels.AddEditFruitViewModel
import ru.turbopro.testworkapp.viewModels.FruitsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FruitsViewModel
    private lateinit var FruitAdapter: FruitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(FruitsViewModel::class.java)
        setViews()
        setObservers()
        setContentView(binding.root)
    }

    private fun setViews() {
            setFruitsAdapter(viewModel.allFruits.value)
            binding.fruitsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = FruitAdapter
            }

        binding.fruitsFabAddFruit.setOnClickListener {
            val intent = Intent(this@MainActivity, AddFruitActivity::class.java)
            startActivity(intent)
        }
        binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
        binding.loaderLayout.circularLoader.showAnimationBehavior
    }

    private fun setObservers() {
        viewModel.storeFruitDataStatus.observe(this) { status ->
            when (status) {
                StoreFruitDataStatus.LOADING -> {
                    binding.loaderLayout.loaderFrameLayout.visibility = View.VISIBLE
                    binding.loaderLayout.circularLoader.showAnimationBehavior
                    binding.fruitsRecyclerView.visibility = View.GONE
                }
                else -> {
                    binding.loaderLayout.circularLoader.hideAnimationBehavior
                    binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                }
            }
            if (status != null && status != StoreFruitDataStatus.LOADING) {
                viewModel.allFruits.observe(this) { FruitsList ->
                    if (FruitsList.isNotEmpty()) {
                        binding.loaderLayout.circularLoader.hideAnimationBehavior
                        binding.loaderLayout.loaderFrameLayout.visibility = View.GONE
                        binding.fruitsRecyclerView.visibility = View.VISIBLE
                        binding.fruitsRecyclerView.adapter?.apply {
                            FruitAdapter.data = FruitsList
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
        viewModel.allFruits.observe(this) {
            if (it.isNotEmpty()) {
                viewModel.setDataLoaded()
            }
        }
    }

    private fun setFruitsAdapter(FruitsList: List<Fruit>?) {
        FruitAdapter = FruitAdapter(FruitsList ?: emptyList(), this)
        FruitAdapter.onClickListener = object : FruitAdapter.OnClickListener {
            override fun onClick(fruitData: Fruit) {
                val intent = Intent(this@MainActivity, FruitDetailsActivity::class.java)
                intent.putExtra("fruitId", fruitData.id)
                startActivity(intent)
            }
        }
    }
}