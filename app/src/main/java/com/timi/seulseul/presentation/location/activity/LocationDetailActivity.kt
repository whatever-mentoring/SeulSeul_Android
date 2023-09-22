package com.timi.seulseul.presentation.location.activity

import android.os.Bundle
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityLocationDetailBinding
import com.timi.seulseul.presentation.common.base.BaseActivity

class LocationDetailActivity : BaseActivity<ActivityLocationDetailBinding>(R.layout.activity_location_detail) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.locationIvBack.setOnClickListener {
            finish()
        }

        binding.locationTvLongAddress.text = intent.getStringExtra("roadAddr")

    }


}