package com.timi.seulseul.presentation.main.adapter

import androidx.recyclerview.widget.DiffUtil
import com.timi.seulseul.data.model.response.BodyData
import com.timi.seulseul.data.model.response.TotalTimeData

sealed class SubwayRouteItem {
    data class TotalTimeSectionItem(val totalTimeData: TotalTimeData) : SubwayRouteItem()
    data class BodyItem(val body: BodyData) : SubwayRouteItem()
    data class TransferItem( val transfer: BodyData) : SubwayRouteItem()
    object Footer : SubwayRouteItem()

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SubwayRouteItem>() {
            override fun areItemsTheSame(oldItem: SubwayRouteItem, newItem: SubwayRouteItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(olditem: SubwayRouteItem, newItem: SubwayRouteItem): Boolean {
                return olditem == newItem
            }
        }
    }
}