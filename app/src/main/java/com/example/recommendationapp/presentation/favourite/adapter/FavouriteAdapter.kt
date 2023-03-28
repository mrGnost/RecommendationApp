package com.example.recommendationapp.presentation.favourite.adapter

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
import com.example.recommendationapp.utils.Common.getPlaceImageAddress
import com.example.recommendationapp.utils.callback.RestaurantClickListener

class FavouriteAdapter(
    private var restaurants: List<RestaurantShort>,
    private var holderClickListener: RestaurantClickListener,
    private var markClickListener: RestaurantClickListener
) : Adapter<FavouriteViewHolder>() {
    private var favourite = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FavouriteViewHolder(inflater.inflate(R.layout.restaurant_holder, parent, false))
    }

    override fun getItemCount(): Int = restaurants.size

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(restaurants[position], favourite, holderClickListener, markClickListener)
    }

    fun setData(newData: List<RestaurantShort>, favourite: Boolean) {
        restaurants = newData
        this.favourite = favourite
        notifyDataSetChanged()
    }
}

class FavouriteViewHolder(itemView: View) : ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.restaurant_image)
    private val placeName: TextView = itemView.findViewById(R.id.restaurant_name)
    private val tags: TextView = itemView.findViewById(R.id.restaurant_tags)
    private val address: TextView = itemView.findViewById(R.id.restaurant_address)
    private val mark: ImageView = itemView.findViewById(R.id.mark)

    fun bind(
        restaurant: RestaurantShort,
        favourite: Boolean,
        holderClickListener: RestaurantClickListener,
        markClickListener: RestaurantClickListener
    ) {
        placeName.text = restaurant.name
        tags.text = restaurant.categories
        address.text = restaurant.address
        if (favourite) {
            mark.setImageResource(R.drawable.ic_favorite_24)
        } else {
            mark.setImageResource(R.drawable.ic_bookmark_24)
        }
        imageView.load(getPlaceImageAddress(restaurant.photo)) {
            crossfade(true)
            error(R.drawable.image_broken_24)
            fallback(R.drawable.image_broken_24)
            placeholder(R.drawable.image_placeholder_24)
            scale(Scale.FIT)
        }
        itemView.setOnClickListener {
            holderClickListener.onClick(restaurant)
        }
        mark.setOnClickListener {
            markClickListener.onClick(restaurant)
        }
    }
}