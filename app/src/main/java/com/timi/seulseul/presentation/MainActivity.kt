package com.timi.seulseul.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.timi.seulseul.MainApplication
import com.timi.seulseul.R
import com.timi.seulseul.data.model.Alarm
import com.timi.seulseul.data.service.LocationService
import com.timi.seulseul.databinding.ActivityMainBinding
import com.timi.seulseul.presentation.apitest.ApiTestActivity
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.dialog.AlarmBottomSheetFragment
import com.timi.seulseul.presentation.dialog.LocationBeforeDialogFragment
import com.timi.seulseul.presentation.location.activity.LocationActivity
import com.timi.seulseul.presentation.main.adapter.SubwayRouteAdapter
import com.timi.seulseul.presentation.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

// Dagger Hilt가 Activity에 의존성을 주입
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var subwayRouteAdapter: SubwayRouteAdapter

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // v1/user post uuid
        viewModel.postAuth()

        binding.apply {
            view = this@MainActivity // xml과 연결
            lifecycleOwner = this@MainActivity
        }

        binding.homeTvLocationSetting.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        binding.goApitestBtn.setOnClickListener {
            startActivity(Intent(this, ApiTestActivity::class.java))
        }

        binding.homeIvSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        // 갱신 날짜 저장 (초기)
        viewModel.saveRefreshDayFirst()

        // 직접 스위치 On, Off 시 상태 변화 적용
        switchNotificationOnOff()

        // LocationService 시작!
        val serviceIntent = Intent(this, LocationService::class.java)
        // API 26 이상 백그라운드 제한 우회 -> ForegroundService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // TODO: 추후 알림 추가하기 버튼으로 옮길 예정 (화면 테스트를 위해 여기에 설정함)
        binding.homeTvDay.setOnClickListener {
            val dialog = LocationBeforeDialogFragment()
            dialog.show(supportFragmentManager, "LocationBeforeDialogFragment")
        }

        // recyclerView
        showSubwayRoute()
    }

    override fun onResume() {
        super.onResume()

        // view에 날짜 갱신
        val dayInfo = viewModel.getTodayDate()
        binding.homeTvDay.text = "${dayInfo.month}월 ${dayInfo.date}일"

        // 갱신 (alarmOn 상태 -> false)
        viewModel.checkRefreshDay()

        checkAlarmSetting()

        val endNickName = BaseActivity.prefs.getString("endNickName", "")

        binding.homeTvLocationSetting.text = endNickName
    }

    private fun checkAlarmSetting() {
        val alarmTime = prefs.getInt("alarmTime", 0)
        val alarmTerm = prefs.getInt("alarmTerm", 0)

        // 알림 설정한 것이 있을 경우
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
        bottomSheetFragment.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.RoundCornerBottomSheetDialogTheme
        )

        if (!bottomSheetFragment.isAdded) {
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        bottomSheetFragment.setButtonClickListener(object :
            AlarmBottomSheetFragment.OnButtonClickListener {
            override fun onOkBtnClicked(time: Int, term: Int) {
                Timber.d("main bottomSheet click listener")

                bottomSheetFragment.dismiss()
                setAlarmAfter()

                // 처음 알람 설정 후 체크 여부 확인
                if (binding.homeClSwitchAlarmOnOff.isChecked) {
                    prefs.edit().putBoolean("alarmOn", true).apply()
                }

                // 첫 알림 설정 여부에 따른 로직 처리
                viewModel.setAlarm()

                // 설정한 알림 데이터 연결 (xml과 연결)
                binding.alarm = Alarm(time, term)
            }
        })
    }

    private fun showSubwayRoute() {
        viewModel.getSubwayRoute()
        viewModel.subwayData.observe(this, Observer {

            if (it != null) {
                subwayRouteAdapter = SubwayRouteAdapter()

                binding.homeRvSubwayRoute.apply {
                    adapter = subwayRouteAdapter
                    layoutManager = LinearLayoutManager(this@MainActivity)
                }

                subwayRouteAdapter.submitList(it)
            }
        })
    }

    private fun setAlarmAfter() {
        binding.homeTvAlarmAdd.visibility = View.GONE
        binding.homeClAlarmSetting.visibility = View.VISIBLE
        binding.homeTvAlarm.text = "알림 수신 예정"
        binding.homeTvDay.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_home_day_checked,
            0,
            0,
            0
        ) // drawableStart 다른 이미지로 변경
    }

    private fun setAlarmBefore() {
        binding.homeTvAlarmAdd.visibility = View.VISIBLE
        binding.homeClAlarmSetting.visibility = View.GONE
        binding.homeTvAlarm.text = "설정된 알림 없음"
        binding.homeTvDay.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_home_day_unchecked,
            0,
            0,
            0
        )
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

            viewModel.patchAlarmEnabled()
        }
    }

    private fun switchOn() {
        binding.homeClAlarmSetting.setBackgroundResource(R.drawable.bg_green_500_main_r8)
        binding.homeClLlTvTitleLastSubway.setTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.green_500_main
            )
        )
        binding.homeClIvAlarm.setImageResource(R.drawable.ic_home_alarm_click)
    }

    private fun switchOff() {
        binding.homeClAlarmSetting.setBackgroundResource(R.drawable.bg_grey_200_r8)
        binding.homeClLlTvTitleLastSubway.setTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.grey_200
            )
        )
        binding.homeClIvAlarm.setImageResource(R.drawable.ic_home_alarm_default)
    }
}