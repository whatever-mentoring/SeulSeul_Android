package com.timi.seulseul.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.timi.seulseul.data.model.SubwayRouteItem
import com.timi.seulseul.data.model.response.BodyData
import com.timi.seulseul.data.model.response.TotalTimeData
import com.timi.seulseul.databinding.ItemSubwayBodyInfoBinding
import com.timi.seulseul.databinding.ItemSubwayFooterBinding
import com.timi.seulseul.databinding.ItemSubwayTotalTimeBinding
import com.timi.seulseul.databinding.ItemSubwayTransferBinding

class SubwayRouteAdapter: ListAdapter<SubwayRouteItem, RecyclerView.ViewHolder>(SubwayRouteItem.DIFF_CALLBACK){

    companion object {
        private const val TOTAL_TIME = 0
        private const val BODY = 1
        private const val TRANSFER = 2
        private const val FOOTER = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TOTAL_TIME ->  TotalTimeViewHolder(ItemSubwayTotalTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            BODY -> BodyViewHolder(ItemSubwayBodyInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TRANSFER -> TransferViewHolder(ItemSubwayTransferBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            FOOTER -> FooterViewHolder(ItemSubwayFooterBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else -> throw IllegalArgumentException("Invalid view type")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is TotalTimeViewHolder -> holder.bind((getItem(position) as SubwayRouteItem.TotalTimeSectionItem).totalTimeData)
        is BodyViewHolder -> holder.bind((getItem(position) as SubwayRouteItem.BodyItem).body)
        is TransferViewHolder-> holder.bind((getItem(position) as SubwayRouteItem.TransferItem).transfer)
        is FooterViewHolder-> {}
        else -> throw IllegalArgumentException("Invalid view holder")
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is SubwayRouteItem.TotalTimeSectionItem -> TOTAL_TIME
        is SubwayRouteItem.BodyItem -> BODY
        is SubwayRouteItem.TransferItem -> TRANSFER
        is SubwayRouteItem.Footer -> FOOTER
        else -> throw IllegalArgumentException("Invalid type of data " + position)
    }
}

class TotalTimeViewHolder(private val binding: ItemSubwayTotalTimeBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: TotalTimeData) {
        binding.totalTime = item
        binding.executePendingBindings()
    }
}

class BodyViewHolder(private val binding: ItemSubwayBodyInfoBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: BodyData) {
        binding.body = item
        binding.executePendingBindings()
    }
}

class TransferViewHolder(private val binding: ItemSubwayTransferBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(item: BodyData){
        binding.transfer = item
        binding.executePendingBindings()
    }
}

class FooterViewHolder(binding: ItemSubwayFooterBinding):RecyclerView.ViewHolder(binding.root)