package com.example.recommendationapp.presentation.restaurant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.recommendationapp.R
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.callback.RestaurantClickListener

class ChainAdapter(
    private val places: List<RestaurantShort>,
    private val clickListener: RestaurantClickListener
) : Adapter<ChainViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChainViewHolder(inflater.inflate(R.layout.chain_holder, parent, false))
    }

    override fun getItemCount(): Int = places.size

    override fun onBindViewHolder(holder: ChainViewHolder, position: Int) {
        holder.bind(places[position], clickListener)
    }
}

class ChainViewHolder(itemView: View) : ViewHolder(itemView) {
    private val placeName: TextView = itemView.findViewById(R.id.restaurant_name)
    private val placeTags: TextView = itemView.findViewById(R.id.restaurant_tags)
    private val placeAddress: TextView = itemView.findViewById(R.id.restaurant_address)

    fun bind(place: RestaurantShort, clickListener: RestaurantClickListener) {
        placeName.text = place.name
        placeTags.text = place.categories
        placeAddress.text = place.address
    }
}