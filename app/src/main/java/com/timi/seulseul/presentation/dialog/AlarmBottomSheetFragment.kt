package com.timi.seulseul.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timi.seulseul.databinding.FragmentAlarmBottomSheetDialogBinding

class AlarmBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding : FragmentAlarmBottomSheetDialogBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAlarmBottomSheetDialogBinding.inflate(inflater, container, false).apply {
            binding = this
            binding?.view = this@AlarmBottomSheetFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT // full screen 으로 만들기 위해 필요1

        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED // full screen 으로 만들기 위해 필요2
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun closeAlarmSetting() {
        dismiss()
    }
}