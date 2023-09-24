package com.timi.seulseul.presentation.location.activity

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.timi.seulseul.R
import com.timi.seulseul.data.model.request.LocationRequest
import com.timi.seulseul.databinding.ActivityLocationDetailBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import timber.log.Timber
import java.io.IOException

class LocationDetailActivity :
    BaseActivity<ActivityLocationDetailBinding>(R.layout.activity_location_detail) {

    companion object {
        const val EXTRA_ROAD_ADDR = "roadAddr"
        const val EXTRA_JIBUN_ADDR = "jibunAddr"
    }

    private var selectedContainer: ConstraintLayout? = null
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val roadAddr = intent.getStringExtra(EXTRA_ROAD_ADDR)
        val jibunAddr = intent.getStringExtra(EXTRA_JIBUN_ADDR)

        Log.d("roadAddr",roadAddr!!)
        Log.d("roadAddr",jibunAddr!!)


        binding.locationIvBack.setOnClickListener {
            finish()
        }

        binding.locationTvLongAddress.text = roadAddr
        binding.locationTvShortAddress.text = jibunAddr

        locationRequest = LocationRequest(
            endX = 0.0,
            endY = 0.0,
            endNickName = "",
            roadNameAddress = roadAddr!!,
            jibunAddress = jibunAddr!!
        )

        geoCode(jibunAddr)

        setupListeners()

    }

    // 지오코딩
    private fun geoCode(address: String) {
        val geocoder = Geocoder(this)
        var result: List<Address>?

        try {
            result = geocoder.getFromLocationName(address, 1)
            if (!result.isNullOrEmpty()) {
                val location = result[0]
                locationRequest.endX = location.latitude
                locationRequest.endY = location.longitude
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

                //컨테이너 누르면 버튼 활성화
                binding.locationDetailBtnComplete.setBackgroundResource(R.drawable.bg_green_500_main_r4)
                binding.locationDetailBtnComplete.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )

                //선택한 컨테이너 뷰 변경
                view.setBackgroundResource(R.drawable.bg_black_r4)
                textViews[i].setTextColor(ContextCompat.getColor(this, R.color.white))
                imageViews[i].first.setImageResource(clickedImages[i])


                // 선택된 컨테이너에서 다른 컨테이너 선택했을 때 defalut 값으로 되돌리기
                if (selectedContainer != null && selectedContainer != view) {

                    selectedContainer?.setBackgroundResource(R.drawable.bg_grey_100_r4)

                    val previousIndex = containers.indexOf(selectedContainer)

                    textViews[previousIndex].setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )

                    imageViews[previousIndex].first.setImageResource(imageViews[previousIndex].second)
                }
                // 현재 선택된 컨테이너 저장
                selectedContainer = view as ConstraintLayout

                locationRequest.endNickName = textViews[i].text.toString()
            }
        }
    }
}
