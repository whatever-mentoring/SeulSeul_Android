package com.timi.seulseul.presentation.location.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityLocationBinding
import com.timi.seulseul.databinding.ActivityLocationDetailBinding
import com.timi.seulseul.presentation.common.base.BaseActivity

class LocationDetailActivity : BaseActivity<ActivityLocationDetailBinding>(R.layout.activity_location_detail) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

    }

    private val ActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val receivedData = data.getStringExtra("data")
                binding.locationTvLongAddress.text = receivedData
            }
        }

    }

}