package ru.turbopro.testworkapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.turbopro.testworkapp.data.Fruit
import ru.turbopro.testworkapp.databinding.FruitListItemBinding

class FruitAdapter(evList: List<Any>, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = evList

    lateinit var onClickListener: OnClickListener

    inner class ItemViewHolder(binding: FruitListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val frCard = binding.fruitCard
        private val frName = binding.fruitTitleTv
        private val fruitImage = binding.fruitImageView

        fun bind(fruitData: Fruit) {
            frCard.setOnClickListener{
                onClickListener.onClick(fruitData)
            }
            frName.text = fruitData.name
            if (fruitData.images.isNotEmpty()) {
                val imgUrl = fruitData.images[0].toUri().buildUpon().scheme("https").build()
                Glide.with(context)
                    .asBitmap()
                    .load(imgUrl)
                    .into(fruitImage)

                fruitImage.clipToOutline = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
                FruitListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val achData = data[position]) {
            is Fruit -> (holder as ItemViewHolder).bind(achData)
        }
    }

    override fun getItemCount(): Int = data.size

    interface OnClickListener {
        fun onClick(FruitData: Fruit)
    }
}