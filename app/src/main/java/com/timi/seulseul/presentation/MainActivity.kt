package com.timi.seulseul.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.timi.seulseul.R
import com.timi.seulseul.data.model.Alarm
import com.timi.seulseul.data.service.LocationService
import com.timi.seulseul.databinding.ActivityMainBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.dialog.AlarmBottomSheetFragment
import com.timi.seulseul.presentation.dialog.LocationBeforeDialogFragment
import com.timi.seulseul.presentation.location.activity.LocationActivity
import com.timi.seulseul.presentation.main.adapter.SubwayRouteAdapter
import com.timi.seulseul.presentation.permission.PermissionActivity
import com.timi.seulseul.presentation.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var subwayRouteAdapter: SubwayRouteAdapter

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            view = this@MainActivity // xml과 연결
            lifecycleOwner = this@MainActivity
        }

        initListener()

        // 직접 스위치 On, Off 시 상태 변화 적용
        switchNotificationOnOff()

        // recyclerView Observer
        setRouteVisibility()

        val serviceIntent = Intent(this, LocationService::class.java)
        // API 26 이상 백그라운드 제한 우회 -> ForegroundService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun hasAllPermissions(): Boolean =
        getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(
                this, it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun getRequiredPermissions(): Array<String> =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )

            else -> arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }


    override fun onResume() {
        super.onResume()

        if (!hasAllPermissions()) {
            Intent(this, PermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(this)
            }
        }

        // 갱신 날짜 저장 (초기)
        viewModel.saveToday()

        // view에 날짜 갱신
        val dayInfo = viewModel.getDayInfo()
        binding.homeTvDay.text = "${dayInfo.month}월 ${dayInfo.date}일"

        // 거주지 닉네임 설정
        val endNickName = BaseActivity.prefs.getString("endNickName", "")
        binding.homeTvLocationSetting.text = endNickName

        // 알림 세팅 체크
        checkAlarmSetting()
    }

    private fun initListener() {
        binding.homeTvLocationSetting.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        binding.homeIvSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    fun checkEndLocation() {
        // onclick (목적지 존재 여부 판단)
        // TODO : ""로 바꾸기
        if (binding.homeTvLocationSetting.text == null) {
            // 목적지 설정 안 했을 때
            val dialog = LocationBeforeDialogFragment()
            dialog.show(supportFragmentManager, "LocationBeforeDialogFragment")
        } else {
            // 목적지 설정 했을 때
            showAlarmSetting()
        }
    }

    private fun checkAlarmSetting() {
        val alarmTime = prefs.getInt("alarmTime", 0)
        val alarmTerm = prefs.getInt("alarmTerm", 0)

        // 알림 설정한 것이 있을 경우
        if (alarmTime != 0) {
            setAlarmAfter()
            binding.alarm = Alarm(alarmTime, alarmTerm)
            checkSwitchSetting() // 알림 설정 이후 스위치 On, Off 여부 확인
        }
    }

    private fun checkSwitchSetting() {
        val alarmOn = prefs.getBoolean("alarmOn", false)

        if (prefs.getString("today", "") == "") {
            viewModel.saveToday()
        }

        // 알림 설정 했고, 갱신 아직 안 했으면 경로 데이터 불러와
        if (prefs.getInt("alarmTime", 0) != 0 || !prefs.getBoolean("refresh", true)) {
            showSubwayRoute()
        }

        if (alarmOn) {
            binding.homeClSwitchAlarmOnOff.isChecked = true
            switchOn()

            // 알림 on 상태 -> '알림 수신 예정' + (todayAlarm <= true)
            setAlarmAfterTitle()
            prefs.edit().putBoolean("todayAlarm", true).apply()

        } else {
            binding.homeClSwitchAlarmOnOff.isChecked = false
            switchOff()

            // 알림 데이터 있고, todayAlarm == false
            if (prefs.getInt("alarmTime", 0) != 0 && !prefs.getBoolean("todayAlarm", false)) {
                Timber.d("경로 show시간 되서 알림 off : ${prefs.getBoolean("todayAlarm", false)}")
                setAlarmBeforeTitle()
            }
        }
    }

    // onclick (알림 추가하기)
    fun showAlarmSetting() {
        val bottomSheetFragment = AlarmBottomSheetFragment()
        bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)

        if (!bottomSheetFragment.isAdded) {
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        bottomSheetFragment.setButtonClickListener(object :
            AlarmBottomSheetFragment.OnButtonClickListener {
            override fun onOkBtnClicked(time: Int, term: Int) {
                bottomSheetFragment.dismiss()
                setAlarmAfter()

                if (!prefs.getBoolean("isNotFirst", false)) {
                    // 처음 알림 ON 상태
                    prefs.edit().putBoolean("alarmOn", true).apply()
                    prefs.edit().putBoolean("refresh", false).apply()

                    // 경로 호출
                    showSubwayRoute()
                }

                // 첫 알림 설정 여부에 따른 로직 처리 (post, patch)
                viewModel.setAlarm()
                checkSwitchSetting()

                // 설정한 알림 데이터 연결 (xml과 연결)
                binding.alarm = Alarm(time, term)
            }
        })
    }

    private fun showSubwayRoute() {
        // 서버 통신 (데이터 받아 오기)
        viewModel.getSubwayRoute()
        viewModel.subwayData.observe(this, Observer { data ->
            if (data != null) {
                subwayRouteAdapter = SubwayRouteAdapter()

                binding.homeRvSubwayRoute.apply {
                    adapter = subwayRouteAdapter
                    layoutManager = LinearLayoutManager(this@MainActivity)
                }

                subwayRouteAdapter.submitList(data)
            }
        })
    }

    private fun setRouteVisibility() {
        // 경로 보여줄 시점 체크
        viewModel.showRoute.observe(this, Observer { show ->
            if (show) {
                // 현재 시간 >= 경로 show 시간
                showRoute()

            } else {
                // 현재 시간 >= 막차 도착 시간
                hideRoute()
            }
        })

        // 경로 show시간 넘어서 -> 설정한 알림이 off 되었을 때
        viewModel.todayAlarmOff.observe(this, Observer { isOff ->
            if (isOff) {
                binding.homeClSwitchAlarmOnOff.isChecked = false
                switchOff()
                setAlarmBeforeTitle()
            }
        })
    }

    private fun setAlarmAfter() {
        binding.homeTvAlarmAdd.visibility = View.GONE
        binding.homeClAlarmSetting.visibility = View.VISIBLE
    }

    private fun setAlarmAfterTitle() {
        binding.homeTvAlarm.text = "알림 수신 예정"
        binding.homeTvDay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_day_checked, 0, 0, 0)
    }

    private fun setAlarmBeforeTitle() {
        binding.homeTvAlarm.text = "설정된 알림 없음"
        binding.homeTvDay.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_day_unchecked, 0, 0, 0)
    }

    private fun switchNotificationOnOff() {
        binding.homeClSwitchAlarmOnOff.setOnClickListener {
            if (binding.homeClSwitchAlarmOnOff.isChecked) {
                switchOn()
                prefs.edit().putBoolean("alarmOn", true).apply()
                prefs.edit().putBoolean("refresh", false).apply()

            } else {
                switchOff()
                prefs.edit().putBoolean("alarmOn", false).apply()
            }
            viewModel.patchAlarmEnabled()
            checkSwitchSetting()
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

    private fun showRoute() {
        binding.homeSubwayRouteTitleBar.visibility = View.VISIBLE
        binding.homeRvSubwayRoute.visibility = View.VISIBLE
        binding.homeLlEmptyView.visibility = View.GONE
        binding.homeViewLine.visibility = View.GONE
    }

    private fun hideRoute() {
        binding.homeSubwayRouteTitleBar.visibility = View.GONE
        binding.homeRvSubwayRoute.visibility = View.GONE
        binding.homeLlEmptyView.visibility = View.VISIBLE
        binding.homeViewLine.visibility = View.VISIBLE
    }
}