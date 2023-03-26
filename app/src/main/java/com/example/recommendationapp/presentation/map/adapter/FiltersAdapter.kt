package com.example.recommendationapp.presentation.map.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.recommendationapp.R
import com.example.recommendationapp.domain.model.Filter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FiltersAdapter(
    private var filters: List<Filter>
) : Adapter<FiltersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FiltersViewHolder(inflater.inflate(R.layout.filter_holder, parent, false))
    }

    override fun getItemCount(): Int = filters.size

    override fun onBindViewHolder(holder: FiltersViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    fun setData(newData: List<Filter>) {
        filters = newData
        notifyDataSetChanged()
    }
}

class FiltersViewHolder(itemView: View) : ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.name)
    private val filterGroup: ChipGroup = itemView.findViewById(R.id.filters_group)

    fun bind(filter: Filter) {
        name.text = filter.name
        for (i in filter.variants.indices) {
            val chip = Chip(itemView.context)
            chip.text = filter.variants[i]
            chip.isChecked = filter.checked[i]
            filterGroup.addView(chip)
        }
    }
}