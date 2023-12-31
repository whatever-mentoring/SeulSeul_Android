package com.timi.seulseul.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityOnBoardingBinding
import com.timi.seulseul.presentation.common.constants.KEY_ONBOARDING
import com.timi.seulseul.presentation.permission.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private val viewModel by viewModels<OnBoardingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (prefs.getBoolean(KEY_ONBOARDING, false)) {
            startActivity(Intent(this, PermissionActivity::class.java))
            finish()
            return  // OnBoardingActivity의 나머지 부분은 실행하지 않음
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)

        binding.viewmodel = viewModel

        binding.lifecycleOwner = this

        viewModel.loadOnBoardingData()

        val viewPager2 = binding.viewPagerOnBoarding
        val dotsIndicator = binding.dotsIndicator

        val adapter = OnBoardingAdapter(this) {
            onConfirmButtonClick()
        }

        viewPager2.adapter = adapter


        dotsIndicator.attachTo(viewPager2)

        viewModel.obLiveData.observe(this) { obLiveData ->
            adapter.submitList(obLiveData)
            dotsIndicator.refreshDots()
        }

        // v1/user post uuid
        viewModel.postAuth()
    }

    private fun onConfirmButtonClick() {

        with(prefs.edit()) {
            putBoolean(KEY_ONBOARDING, true)
            apply()
        }

        startActivity(Intent(this, PermissionActivity::class.java))
        finish()
    }
}