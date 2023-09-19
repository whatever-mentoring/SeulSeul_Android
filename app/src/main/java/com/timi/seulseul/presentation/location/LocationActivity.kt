package com.timi.seulseul.presentation.location

import android.os.Bundle
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityMainBinding
import com.timi.seulseul.presentation.common.base.BaseActivity

class LocationActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_location_search) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)


    }
}