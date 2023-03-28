package com.example.recommendationapp.presentation.restaurant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import coil.size.Scale
import com.example.recommendationapp.R
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.Common
import com.example.recommendationapp.utils.callback.RestaurantClickListener

class SimilarAdapter(
    private val places: List<RestaurantShort>,
    private val clickListener: RestaurantClickListener
) : Adapter<SimilarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SimilarViewHolder(inflater.inflate(R.layout.similar_holder, parent, false))
    }

    override fun getItemCount(): Int = places.size

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
        holder.bind(places[position], clickListener)
    }

}

class SimilarViewHolder(itemView: View) : ViewHolder(itemView) {
    private val placeImage: ImageView = itemView.findViewById(R.id.place_image)
    private val placeName: TextView = itemView.findViewById(R.id.place_name)

    fun bind(place: RestaurantShort, clickListener: RestaurantClickListener) {
        placeName.text = place.name
        placeImage.load(Common.getPlaceImageAddress(place.photo)) {
            crossfade(true)
            error(R.drawable.image_broken_24)
            fallback(R.drawable.image_broken_24)
            placeholder(R.drawable.image_placeholder_24)
            scale(Scale.FIT)
        }
        itemView.setOnClickListener {
            clickListener.onClick(place)
        }
    }
}