package com.timi.seulseul.presentation.location.activity

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityLocationDetailBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.location.viewmodel.LocationDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


@AndroidEntryPoint
class LocationDetailActivity :
    BaseActivity<ActivityLocationDetailBinding>(R.layout.activity_location_detail) {

    private val viewModel by viewModels<LocationDetailViewModel>()

    private var selectedContainer: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.locationIvBack.setOnClickListener {
            finish()
        }

        val roadAddr = intent.getStringExtra(LocationSearchActivity.EXTRA_ROAD_ADDR)
        val jibunAddr = intent.getStringExtra(LocationSearchActivity.EXTRA_JIBUN_ADDR)

        viewModel.setAddress(roadAddr!!, jibunAddr!!)

        binding.locationTvLongAddress.text = viewModel.roadAddr
        binding.locationTvShortAddress.text = viewModel.jibunAddr


        geoCode(viewModel.jibunAddr)
        setupListeners()

        binding.locationDetailBtnComplete.setOnClickListener {
            if (selectedContainer != null) {
                viewModel.postEndLocation(viewModel.locationRequest)
                Log.d("locationDetailActivity", "완료버튼")
                finish()
            }
        }


    }

    // 지오코딩
    private fun geoCode(address: String) {
        val geocoder = Geocoder(this)
        val result: List<Address>?

        try {
            result = geocoder.getFromLocationName(address, 1)
            if (!result.isNullOrEmpty()) {
                val location = result[0]
                viewModel.locationRequest.endX = location.longitude
                viewModel.locationRequest.endY = location.latitude
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

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
            Pair(
                binding.locationDetailIvDormitory,
                R.drawable.ic_location_detail_dormitory_default
            ),
            Pair(binding.locationDetailIvEtc, R.drawable.ic_location_detail_etc_default)
        )

        val clickedImages = listOf(
            R.drawable.ic_location_detail_home_click,
            R.drawable.ic_location_detail_one_room_click,
            R.drawable.ic_location_detail_dormitory_click,
            R.drawable.ic_location_detail_etc_click
        )

        for (i in containers.indices) {

            containers[i].setOnClickListener { view ->

                // 모든 컨테이너 초기화
                for (i in containers.indices) {
                    containers[i].setBackgroundResource(R.drawable.bg_grey_100_r4)
                    textViews[i].setTextColor(ContextCompat.getColor(this, R.color.black))
                    imageViews[i].first.setImageResource(imageViews[i].second)
                }

                //컨테이너 누르면 버튼 활성화
                binding.locationDetailBtnComplete.setBackgroundResource(R.drawable.bg_green_500_main_r4)
                binding.locationDetailBtnComplete.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )

                // 선택한 컨테이너만 색상 변경
                view.setBackgroundResource(R.drawable.bg_black_r4)
                textViews[i].setTextColor(ContextCompat.getColor(this, R.color.white))
                imageViews[i].first.setImageResource(clickedImages[i])

                // 현재 선택된 컨테이너 저장
                selectedContainer = view as ConstraintLayout

                viewModel.locationRequest.endNickName = textViews[i].text.toString()
            }
        }
    }
}
