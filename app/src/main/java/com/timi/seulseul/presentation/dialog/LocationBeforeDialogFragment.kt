package com.timi.seulseul.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.timi.seulseul.databinding.FragmentLocationBeforeDialogBinding

class LocationBeforeDialogFragment : DialogFragment() {

    private var _binding : FragmentLocationBeforeDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentLocationBeforeDialogBinding.inflate(inflater, container, false).apply {
            _binding = this
            view = this@LocationBeforeDialogFragment
            binding.lifecycleOwner = this@LocationBeforeDialogFragment

            // 레이아웃 배경 투명
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun closeDialog() {
        dismiss()
    }
}