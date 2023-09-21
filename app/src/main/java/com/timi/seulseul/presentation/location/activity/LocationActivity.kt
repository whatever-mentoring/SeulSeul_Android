package com.timi.seulseul.presentation.location.activity

import android.content.Intent
import android.os.Bundle
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityLocationBinding
import com.timi.seulseul.presentation.common.base.BaseActivity

class LocationActivity : BaseActivity<ActivityLocationBinding>(R.layout.activity_location) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.locationSearchBar.setOnClickListener {
            startActivity(Intent(this,LocationSearchActivity::class.java))
        }

        binding.locationIvBack.setOnClickListener {
            finish()
        }
    }

}