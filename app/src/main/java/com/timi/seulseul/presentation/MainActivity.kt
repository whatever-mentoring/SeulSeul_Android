package com.timi.seulseul.presentation

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.timi.seulseul.R
import com.timi.seulseul.data.model.Alarm
import com.timi.seulseul.databinding.ActivityMainBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.dialog.AlarmBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("create")

        binding.apply {
            view = this@MainActivity // xml과 연결
            lifecycleOwner = this@MainActivity
        }

        // 초기 알림 설정 여부 확인
        checkAlarmSetting()

        // 직접 스위치 On, Off 시 상태 변화 적용
        switchNotificationOnOff()
    }

    private fun checkAlarmSetting() {
        val alarmTime = prefs.getInt("alarmTime", 0)
        val alarmTerm = prefs.getInt("alarmTerm", 0)

        if (alarmTime != 0) {
            setAlarmAfter()
            binding.alarm = Alarm(alarmTime, alarmTerm)
            checkSwitchSetting() // 알림 설정 이후 스위치 On, Off 여부 확인
        } else {
            setAlarmBefore()
        }
    }

    private fun checkSwitchSetting() {
        val alarmOn = prefs.getBoolean("alarmOn", false)

        if (alarmOn) {
            binding.homeClSwitchAlarmOnOff.isChecked = true
            switchOn()
        } else {
            binding.homeClSwitchAlarmOnOff.isChecked = false
            switchOff()
        }
    }

    // onclick (알림 추가하기)
    fun showAlarmSetting() {
        val bottomSheetFragment = AlarmBottomSheetFragment()
        bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)

        if (!bottomSheetFragment.isAdded) {
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        bottomSheetFragment.setButtonClickListener(object : AlarmBottomSheetFragment.OnButtonClickListener {
            override fun onOkBtnClicked(time: Int, term: Int) {
                bottomSheetFragment.dismiss()
                setAlarmAfter()

                // 처음 알람 설정 후 체크 여부 확인
                if (binding.homeClSwitchAlarmOnOff.isChecked) {
                    prefs.edit().putBoolean("alarmOn", true).apply()
                } else {
                    prefs.edit().putBoolean("alarmOn", false).apply()
                }

                // 설정한 알림 데이터 연결
                binding.alarm = Alarm(time, term)
            }
        })
    }

    private fun setAlarmAfter() {
        binding.homeTvAlarmAdd.visibility = View.GONE
        binding.homeClAlarmSetting.visibility = View.VISIBLE
        binding.homeTvAlarm.text = "알림 수신 예정"
        binding.homeTvDay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_day_checked, 0, 0, 0) // drawableStart 다른 이미지로 변경
    }

    private fun setAlarmBefore() {
        binding.homeTvAlarmAdd.visibility = View.VISIBLE
        binding.homeClAlarmSetting.visibility = View.GONE
        binding.homeTvAlarm.text = "설정된 알림 없음"
        binding.homeTvDay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_day_unchecked, 0, 0, 0)
    }

    private fun switchNotificationOnOff() {
        binding.homeClSwitchAlarmOnOff.setOnCheckedChangeListener { _, onSwitch ->
            if (onSwitch) {
                switchOn()
                prefs.edit().putBoolean("alarmOn", true).apply()

            } else {
                switchOff()
                prefs.edit().putBoolean("alarmOn", false).apply()
            }
        }
    }

    private fun switchOn() {
        binding.homeClAlarmSetting.setBackgroundResource(R.drawable.bg_green_500_main_r8)
        binding.homeClLlTvTitleLastSubway.setTextColor(ContextCompat.getColor(applicationContext, R.color.green_500_main))
        binding.homeClIvAlarm.setImageResource(R.drawable.ic_home_alarm_click)
    }

    private fun switchOff() {
        binding.homeClAlarmSetting.setBackgroundResource(R.drawable.bg_grey_200_r8)
        binding.homeClLlTvTitleLastSubway.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey_200))
        binding.homeClIvAlarm.setImageResource(R.drawable.ic_home_alarm_default)
    }
}