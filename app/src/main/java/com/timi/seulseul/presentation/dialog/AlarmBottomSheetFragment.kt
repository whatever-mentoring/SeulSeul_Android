package com.timi.seulseul.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.R
import com.timi.seulseul.databinding.FragmentAlarmBottomSheetDialogBinding
import timber.log.Timber

class AlarmBottomSheetFragment : BottomSheetDialogFragment() {


    private var _binding: FragmentAlarmBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private var isChecking = true

    private var alarmTime : Int = 0
    private var alarmTerm : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAlarmBottomSheetDialogBinding.inflate(inflater, container, false).apply {
            _binding = this
            binding.view = this@AlarmBottomSheetFragment
            binding.lifecycleOwner = this@AlarmBottomSheetFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomSheet()
        initListener()

        alarmTime = prefs.getInt("alarmTime", 0)
        alarmTerm = prefs.getInt("alarmTerm", 0)
        Timber.d("onViewCreated : $alarmTime / $alarmTerm")

        // 데이터 체크
        setAlarmData()

        // radioButton 세팅
        checkAlarmTime()
        checkAlarmTerm()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initBottomSheet() {
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT // full screen 으로 만들기 위해 필요1

        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED // full screen 으로 만들기 위해 필요2
    }

    fun initListener() {
        binding.homeClAlarmBottomSheetTvSettingOk.setOnClickListener{
            buttonClickListener.onOkBtnClicked()
            closeBottomSheet() // 알림 설정 버튼 눌렀을 떄
        }
    }

    private fun setAlarmData() {
        when(alarmTime) {
            60 -> binding.itemPreferredTimeRbBeforeOneHour.isChecked = true
            90 -> binding.itemPreferredTimeRbBeforeOneHourHalf.isChecked = true
            120 -> binding.itemPreferredTimeRbBeforeTwoHour.isChecked = true
            150 -> binding.itemPreferredTimeRbBeforeTwoHourHalf.isChecked = true
            180 -> binding.itemPreferredTimeRbBeforeThreeHour.isChecked = true
        }

        when(alarmTerm) {
            10 -> binding.itemTimeTermRb10.isChecked = true
            20 -> binding.itemTimeTermRb20.isChecked = true
            30 -> binding.itemTimeTermRb30.isChecked = true
            40 -> binding.itemTimeTermRb40.isChecked = true
            50 -> binding.itemTimeTermRb50.isChecked = true
            60 -> binding.itemTimeTermRb60.isChecked = true
        }

        // 알림 설정 유무 따른 버튼 설정
        if (alarmTime != 0) {
            binding.homeClAlarmBottomSheetTvSettingOk.setBackgroundResource(R.drawable.bg_green_500_main_r4)
            binding.homeClAlarmBottomSheetTvSettingOk.isEnabled = true
        } else {
            binding.homeClAlarmBottomSheetTvSettingOk.setBackgroundResource(R.drawable.bg_grey_200_r4)
            binding.homeClAlarmBottomSheetTvSettingOk.isEnabled = false
        }
    }

    // 알람 희망 시간
    private fun checkAlarmTime() {
        binding.homeClAlarmBottomSheetRgPreferredTime.setOnCheckedChangeListener { group, i ->
            when(i) {
                R.id.item_preferred_time_rb_before_one_hour -> alarmTime = 60
                R.id.item_preferred_time_rb_before_one_hour_half -> alarmTime = 90
                R.id.item_preferred_time_rb_before_two_hour -> alarmTime = 120
                R.id.item_preferred_time_rb_before_two_hour_half -> alarmTime = 150
                R.id.item_preferred_time_rb_before_three_hour -> alarmTime = 180
            }

            // 알림 희망 시간 선택 시 알림 설정 버튼 색 변경
            if (i > -1) {
                binding.homeClAlarmBottomSheetTvSettingOk.setBackgroundResource(R.drawable.bg_green_500_main_r4)
                binding.homeClAlarmBottomSheetTvSettingOk.isEnabled = true
            }
        }
    }

    // 알림 반복
    private fun checkAlarmTerm() {
        binding.homeClAlarmBottomSheetRgTerm1.setOnCheckedChangeListener { group, i ->
            if (i != -1 && isChecking) {
                isChecking = false
                binding.homeClAlarmBottomSheetRgTerm2.clearCheck()
            }
            isChecking = true
            when(i) {
                R.id.item_time_term_rb_10 -> alarmTerm = 10
                R.id.item_time_term_rb_20 -> alarmTerm = 20
                R.id.item_time_term_rb_30 -> alarmTerm = 30
            }
        }

        binding.homeClAlarmBottomSheetRgTerm2.setOnCheckedChangeListener { group, i ->
            if (i != -1 && isChecking) {
                isChecking = false
                binding.homeClAlarmBottomSheetRgTerm1.clearCheck()
            }
            isChecking = true
            when(i) {
                R.id.item_time_term_rb_40 -> alarmTerm = 40
                R.id.item_time_term_rb_50 -> alarmTerm = 50
                R.id.item_time_term_rb_60 -> alarmTerm = 60
            }
        }
    }

    private fun closeBottomSheet() {
        prefs.edit().putInt("alarmTime", alarmTime).apply()
        prefs.edit().putInt("alarmTerm", alarmTerm).apply()
        //prefs.edit().putBoolean("alarmOn", true).apply()
        Timber.d("closeBottomSheet : $alarmTime / $alarmTerm")
    }

    interface OnButtonClickListener {
        fun onOkBtnClicked()
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}