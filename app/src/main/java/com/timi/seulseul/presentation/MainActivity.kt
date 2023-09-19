package com.timi.seulseul.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityMainBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.dialog.AlarmBottomSheetFragment
import com.timi.seulseul.presentation.location.LocationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var bottomSheetFragment: BottomSheetDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding.view = this

        initBottomSheetFragment()

        binding.homeTvLocationSetting.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initBottomSheetFragment() {
        bottomSheetFragment = AlarmBottomSheetFragment()
        bottomSheetFragment.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.RoundCornerBottomSheetDialogTheme
        )
    }

    fun showAlarmSetting() {
        if (!bottomSheetFragment.isAdded) {
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }
}