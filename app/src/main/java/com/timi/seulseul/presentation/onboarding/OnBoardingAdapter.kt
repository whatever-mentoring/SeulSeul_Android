package com.timi.seulseul.presentation.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.timi.seulseul.data.model.OnBoardingData
import com.timi.seulseul.databinding.ItemOnBoardingBinding

class OnBoardingAdapter(
    private val context: Context,
    private val onConfirmButtonClick: () -> Unit // Lambda function instead of interface
) : ListAdapter<OnBoardingData, OnBoardingAdapter.PageViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OnBoardingData>() {
            override fun areItemsTheSame(oldItem: OnBoardingData, newItem: OnBoardingData): Boolean =
                oldItem.imageResId == newItem.imageResId && oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: OnBoardingData, newItem: OnBoardingData): Boolean =
                oldItem == newItem
        }
    }

    inner class PageViewHolder(val binding: ItemOnBoardingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(page: OnBoardingData, lastPage:Boolean) {
            binding.page = page
            binding.lastPage = lastPage

            if (position == 0) {
                binding.onboardingTvSearchbar.visibility = View.VISIBLE
            } else {
                binding.onboardingTvSearchbar.visibility = View.GONE
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): PageViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemOnBoardingBinding.inflate(inflater,parent,false)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position:Int){
        holder.bind(getItem(position), position == itemCount - 1)

        // 현재 페이지가 마지막 페이지인 경우 버튼 클릭 리스너 설정합니다.
        if (position == itemCount - 1){
            holder.binding.btnOnBoarding.setOnClickListener { onConfirmButtonClick() }
        }
    }

}