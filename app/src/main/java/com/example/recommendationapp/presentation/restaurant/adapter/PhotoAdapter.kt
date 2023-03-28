package com.example.recommendationapp.presentation.restaurant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import coil.size.Scale
import com.example.recommendationapp.R
import com.example.recommendationapp.utils.Common

class PhotoAdapter(
    private val dishes: List<Int>
) : Adapter<PhotoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(inflater.inflate(R.layout.photo_holder, parent, false))
    }

    override fun getItemCount(): Int = dishes.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(dishes[position])
    }

}

class PhotoViewHolder(itemView: View) : ViewHolder(itemView) {
    private val image: ImageView = itemView.findViewById(R.id.dish_image)

    fun bind(dish: Int) {
        image.load(Common.getDishImageAddress(dish)) {
            crossfade(true)
            error(R.drawable.image_broken_24)
            fallback(R.drawable.image_broken_24)
            placeholder(R.drawable.image_placeholder_24)
            scale(Scale.FIT)
        }
    }
}