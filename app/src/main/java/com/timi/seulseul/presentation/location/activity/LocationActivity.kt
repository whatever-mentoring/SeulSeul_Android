package com.timi.seulseul.presentation.location.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityLocationBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.location.adapter.locationAdapter
import com.timi.seulseul.presentation.location.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationActivity : BaseActivity<ActivityLocationBinding>(R.layout.activity_location) {

    private val viewModel by viewModels<LocationViewModel>()
    private val adapter = locationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.locationRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.locationRecyclerView.adapter = adapter

        viewModel.endLocationData.observe(this) { locations ->
            adapter.submitList(locations)
        }

        viewModel.getEndLocation()

        adapter.onItemClickListener = { location ->
            viewModel.setEndLocation(location.id)

            with(BaseActivity.prefs.edit()) {
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
        viewModel.getEndLocation()
    }

}