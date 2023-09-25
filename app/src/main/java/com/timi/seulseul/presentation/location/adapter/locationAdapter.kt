package com.timi.seulseul.presentation.location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.timi.seulseul.data.model.response.GetEndLocationData
import com.timi.seulseul.databinding.ItemLocationDetailBinding

class locationAdapter : ListAdapter<GetEndLocationData, LocationViewHolder>(LocationDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLocationDetailBinding.inflate(layoutInflater)

        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)

        holder.binding.location = location
        holder.binding.executePendingBindings()
    }

    object LocationDiffCallback : DiffUtil.ItemCallback<GetEndLocationData>() {

        override fun areItemsTheSame(oldItem: GetEndLocationData, newItem: GetEndLocationData): Boolean {
            return oldItem.id == newItem.id // Assuming 'id' is a unique identifier for each item.
        }

        override fun areContentsTheSame(oldItem: GetEndLocationData, newItem: GetEndLocationData): Boolean {
            return oldItem == newItem // This might need to be changed based on your specific use case.
        }
    }
}

class LocationViewHolder(val binding: ItemLocationDetailBinding) :
    RecyclerView.ViewHolder(binding.root)
