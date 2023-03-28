package com.example.recommendationapp.presentation.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.example.recommendationapp.R
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.Common
import com.example.recommendationapp.utils.callback.RestaurantClickListener

class SearchAdapter(
    private var restaurants: List<RestaurantShort>,
    private var holderClickListener: RestaurantClickListener,
    private var markClickListener: RestaurantClickListener
) : RecyclerView.Adapter<SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchViewHolder(inflater.inflate(R.layout.restaurant_holder, parent, false))
    }

    override fun getItemCount(): Int = restaurants.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(restaurants[position], holderClickListener, markClickListener)
    }

    fun setData(newData: List<RestaurantShort>) {
        restaurants = newData
        notifyDataSetChanged()
    }
}

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.restaurant_image)
    private val placeName: TextView = itemView.findViewById(R.id.restaurant_name)
    private val tags: TextView = itemView.findViewById(R.id.restaurant_tags)
    private val address: TextView = itemView.findViewById(R.id.restaurant_address)
    private val mark: ImageView = itemView.findViewById(R.id.mark)

    fun bind(
        restaurant: RestaurantShort,
        holderClickListener: RestaurantClickListener,
        markClickListener: RestaurantClickListener
    ) {
        placeName.text = restaurant.name
        tags.text = restaurant.categories
        address.text = restaurant.address
        if (restaurant.favourite)
            mark.setImageResource(R.drawable.ic_favorite_24)
        else
            mark.setImageResource(R.drawable.ic_favorite_border_24)
        imageView.load(Common.getPlaceImageAddress(restaurant.photo)) {
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