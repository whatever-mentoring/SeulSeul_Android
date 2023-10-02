package com.timi.seulseul.presentation.location.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityLocationBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.location.adapter.locationAdapter
import com.timi.seulseul.presentation.location.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationActivity : BaseActivity<ActivityLocationBinding>(R.layout.activity_location) {

    private val viewModel by viewModels<LocationViewModel>()
    private val adapter = locationAdapter()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("locationActivity", "onCreate() 실행")

        binding.locationRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.locationRecyclerView.adapter = adapter

        viewModel.endLocationData.observe(this) { locations ->
            adapter.submitList(locations)
        }

        viewModel.getEndLocation()

        adapter.onItemClickListener = { location ->
            viewModel.setEndLocation(location.id)

            //id 값의 endNickName 받아오기
            with(prefs.edit()) {
                putString("endNickName", location.endNickName)
                apply()
            }
            finish()
        }

        binding.locationSearchBar.setOnClickListener {
            startActivity(Intent(this, LocationSearchActivity::class.java))
        }

        binding.locationIvBack.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()

        coroutineScope.launch {
            delay(1700)
            viewModel.getEndLocation()
        }


    }

}