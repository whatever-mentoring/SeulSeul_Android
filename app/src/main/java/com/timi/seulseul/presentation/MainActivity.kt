package com.timi.seulseul.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.timi.seulseul.R
import com.timi.seulseul.databinding.ActivityMainBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.dialog.AlarmBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            view = this@MainActivity // xml과 연결
            lifecycleOwner = this@MainActivity
        }

        // 초기 알림 여부 확인
        checkAlarmSetting()
    }

    private fun checkAlarmSetting() {
        val alarmTime = prefs.getInt("alarmTime", 0)

        if (alarmTime != 0) afterSetAlarm() else beforeSetAlarm()
    }

    // onclick (알림 추가하기)
    fun showAlarmSetting() {
        val bottomSheetFragment = AlarmBottomSheetFragment()
        bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)

        bottomSheetFragment.setButtonClickListener(object : AlarmBottomSheetFragment.OnButtonClickListener {
            override fun onOkBtnClicked() {
                bottomSheetFragment.dismiss()
                afterSetAlarm()
            }
        })

        if (!bottomSheetFragment.isAdded) {
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun afterSetAlarm() {
        binding.homeTvAlarmAdd.visibility = View.GONE
        binding.homeClAlarmSetting.visibility = View.VISIBLE
        binding.homeTvAlarm.text = "알림 수신 예정"
        binding.homeTvDay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_day_checked, 0, 0, 0) // drawableStart 다른 이미지로 변경
    }

    private fun beforeSetAlarm() {
        binding.homeTvAlarmAdd.visibility = View.VISIBLE
        binding.homeClAlarmSetting.visibility = View.GONE
        binding.homeTvAlarm.text = "설정된 알림 없음"
        binding.homeTvDay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_day_unchecked, 0, 0, 0)
    }

}