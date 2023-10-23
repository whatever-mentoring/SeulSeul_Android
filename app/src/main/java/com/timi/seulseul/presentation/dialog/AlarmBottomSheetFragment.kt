package com.timi.seulseul.presentation.dialog

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.R
import com.timi.seulseul.databinding.FragmentAlarmBottomSheetDialogBinding
import com.timi.seulseul.presentation.common.constants.ALARM_TERM
import com.timi.seulseul.presentation.common.constants.ALARM_TIME
import timber.log.Timber

class AlarmBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAlarmBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

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

        // bottom sheet full screen 설정
        initBottomSheet()

        // 알림 설정 버튼 눌렀을 떄
        initListener()

        // 기존 알림 데이터 가져오기
        alarmTime = prefs.getInt(ALARM_TIME, 0)
        alarmTerm = prefs.getInt(ALARM_TERM, 0)
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

        changeTextColor() // RadioButton 앞에 '막차 기준' 색 변경
    }

    private fun changeTextColor() {
        val builder1 = SpannableStringBuilder(binding.itemPreferredTimeRbBeforeOneHour.text.toString())
        val builder2 = SpannableStringBuilder(binding.itemPreferredTimeRbBeforeOneHourHalf.text.toString())
        val builder3 = SpannableStringBuilder(binding.itemPreferredTimeRbBeforeTwoHour.text.toString())
        val builder4 = SpannableStringBuilder(binding.itemPreferredTimeRbBeforeTwoHourHalf.text.toString())
        val builder5 = SpannableStringBuilder(binding.itemPreferredTimeRbBeforeThreeHour.text.toString())

        val colorGrey = ForegroundColorSpan(Color.parseColor("#666666"))

        builder1.setSpan(colorGrey, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder2.setSpan(colorGrey, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder3.setSpan(colorGrey, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder4.setSpan(colorGrey, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder5.setSpan(colorGrey, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.itemPreferredTimeRbBeforeOneHour.text = builder1
        binding.itemPreferredTimeRbBeforeOneHourHalf.text = builder2
        binding.itemPreferredTimeRbBeforeTwoHour.text = builder3
        binding.itemPreferredTimeRbBeforeTwoHourHalf.text = builder4
        binding.itemPreferredTimeRbBeforeThreeHour.text = builder5
    }

    private fun initListener() {
        binding.homeClAlarmBottomSheetTvSettingOk.setOnClickListener{
            // 설정한 알림 데이터 저장
            prefs.edit().putInt(ALARM_TIME, alarmTime).apply()
            prefs.edit().putInt(ALARM_TERM, alarmTerm).apply()
            Timber.d("closeBottomSheet : $alarmTime / $alarmTerm")

            // main으로 설정한 데이터 전달
            buttonClickListener.onOkBtnClicked(alarmTime, alarmTerm)
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
            10 -> binding.itemTimeTermCb10.isChecked = true
            20 -> binding.itemTimeTermCb20.isChecked = true
            30 -> binding.itemTimeTermCb30.isChecked = true
            40 -> binding.itemTimeTermCb40.isChecked = true
            50 -> binding.itemTimeTermCb50.isChecked = true
            60 -> binding.itemTimeTermCb60.isChecked = true
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
        binding.itemTimeTermCb10.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmTerm = 10
                binding.itemTimeTermCb20.isChecked = false
                binding.itemTimeTermCb30.isChecked = false
                binding.itemTimeTermCb40.isChecked = false
                binding.itemTimeTermCb50.isChecked = false
                binding.itemTimeTermCb60.isChecked = false
            } else {
                checkAlarmTermEmpty()
            }
        }

        binding.itemTimeTermCb20.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmTerm = 20
                binding.itemTimeTermCb10.isChecked = false
                binding.itemTimeTermCb30.isChecked = false
                binding.itemTimeTermCb40.isChecked = false
                binding.itemTimeTermCb50.isChecked = false
                binding.itemTimeTermCb60.isChecked = false
            } else {
                checkAlarmTermEmpty()
            }
        }

        binding.itemTimeTermCb30.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmTerm = 30
                binding.itemTimeTermCb10.isChecked = false
                binding.itemTimeTermCb20.isChecked = false
                binding.itemTimeTermCb40.isChecked = false
                binding.itemTimeTermCb50.isChecked = false
                binding.itemTimeTermCb60.isChecked = false
            } else {
                checkAlarmTermEmpty()
            }
        }

        binding.itemTimeTermCb40.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmTerm = 40
                binding.itemTimeTermCb10.isChecked = false
                binding.itemTimeTermCb20.isChecked = false
                binding.itemTimeTermCb30.isChecked = false
                binding.itemTimeTermCb50.isChecked = false
                binding.itemTimeTermCb60.isChecked = false
            } else {
                checkAlarmTermEmpty()
            }
        }

        binding.itemTimeTermCb50.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmTerm = 50
                binding.itemTimeTermCb10.isChecked = false
                binding.itemTimeTermCb20.isChecked = false
                binding.itemTimeTermCb30.isChecked = false
                binding.itemTimeTermCb40.isChecked = false
                binding.itemTimeTermCb60.isChecked = false
            } else {
                checkAlarmTermEmpty()
            }
        }

        binding.itemTimeTermCb60.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmTerm = 60
                binding.itemTimeTermCb10.isChecked = false
                binding.itemTimeTermCb20.isChecked = false
                binding.itemTimeTermCb30.isChecked = false
                binding.itemTimeTermCb40.isChecked = false
                binding.itemTimeTermCb50.isChecked = false
            } else {
                checkAlarmTermEmpty()
            }
        }
    }

    private fun checkAlarmTermEmpty() {
        if (!binding.itemTimeTermCb10.isChecked && !binding.itemTimeTermCb20.isChecked && !binding.itemTimeTermCb30.isChecked &&
            !binding.itemTimeTermCb40.isChecked && !binding.itemTimeTermCb50.isChecked && !binding.itemTimeTermCb60.isChecked) {
            alarmTerm = 0
        }
    }

    interface OnButtonClickListener {
        fun onOkBtnClicked(time : Int, term : Int)
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}