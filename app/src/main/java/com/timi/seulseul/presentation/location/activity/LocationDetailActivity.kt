package com.timi.seulseul.presentation.location.activity

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.timi.seulseul.R
import com.timi.seulseul.data.model.request.LocationRequest
import com.timi.seulseul.databinding.ActivityLocationDetailBinding
import com.timi.seulseul.presentation.common.base.BaseActivity

class LocationDetailActivity : BaseActivity<ActivityLocationDetailBinding>(R.layout.activity_location_detail) {

    private var selectedContainer: ConstraintLayout? = null
    private var locationRequest : LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.locationIvBack.setOnClickListener {
            finish()
        }

        binding.locationTvLongAddress.text = intent.getStringExtra("roadAddr")
        binding.locationTvShortAddress.text = intent.getStringExtra("jibun")

        setupListeners()

    }

    private fun setupListeners() {
        val containers = listOf(
            binding.locationDetailContainerHome,
            binding.locationDetailContainerOneRoom,
            binding.locationDetailContainerDormitory,
            binding.locationDetailContainerEtc
        )

        val textViews = listOf(
            binding.locationDetailTvHome,
            binding.locationDetailTvOneRoom,
            binding.locationDetailTvDormitory,
            binding.locationDetailTvEtc
        )

        val imageViews = listOf(
            Pair(binding.locationDetailIvHome, R.drawable.ic_location_detail_home_default),
            Pair(binding.locationDetailIvOneRoom, R.drawable.ic_location_detail_one_room_default),
            Pair(binding.locationDetailIvDormitory, R.drawable.ic_location_detail_dormitory_default),
            Pair(binding.locationDetailIvEtc,R.drawable.ic_location_detail_etc_default)
        )

        val clickedImages = listOf(
            R.drawable.ic_location_detail_home_click,
            R.drawable.ic_location_detail_one_room_click,
            R.drawable.ic_location_detail_dormitory_click,
            R.drawable.ic_location_detail_etc_click
        )

        for (i in containers.indices) {

            containers[i].setOnClickListener { view ->

                // 선택한 컨테이너의 배경색을 변경
                view.setBackgroundResource(R.drawable.bg_black_r4)

                // 선택한 컨테이너의 텍스트 색상을 변경
                textViews[i].setTextColor(ContextCompat.getColor(this,R.color.white))

                // 선택한 컨테이너의 이미지를 변경
                imageViews[i].first.setImageResource(clickedImages[i])

                if (selectedContainer != null && selectedContainer != view) {

                    // 이전에 선택했던 컨테이너(있다면)의 배경색을 원래대로 되돌립니다.
                    selectedContainer?.setBackgroundResource(R.drawable.bg_grey_100_r4)

                    // 이전에 선택했던 컨테이너 색상 원래대로
                    val previousIndex = containers.indexOf(selectedContainer)

                    textViews[previousIndex].setTextColor(ContextCompat.getColor(this,R.color.black))

                    // 이전에 선택했던 컨테이너 이미지 원래대로
                    imageViews[previousIndex].first.setImageResource(imageViews[previousIndex].second)
                }

                // 현재 선택된 컨테이너 저장
                selectedContainer = view as ConstraintLayout

            }
        }
    }
}
