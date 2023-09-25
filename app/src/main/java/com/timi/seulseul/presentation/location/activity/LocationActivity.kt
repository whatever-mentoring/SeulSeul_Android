package com.timi.seulseul.presentation.location.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityLocationBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.location.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationActivity : BaseActivity<ActivityLocationBinding>(R.layout.activity_location) {

    private val viewModel by viewModels<LocationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getEndLocation()

        binding.locationSearchBar.setOnClickListener {
            startActivity(Intent(this,LocationSearchActivity::class.java))
        }

        binding.locationIvBack.setOnClickListener {
            finish()
        }
    }

}