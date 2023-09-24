package com.timi.seulseul.presentation.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timi.seulseul.R
import com.timi.seulseul.data.model.OnBoardingData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(

) : ViewModel() {

    private var _obLiveData = MutableLiveData<List<OnBoardingData>>()
    val obLiveData: LiveData<List<OnBoardingData>> get() = _obLiveData

    fun loadOnBoardingData() {
        _obLiveData.value = listOf(
            OnBoardingData(R.drawable.img_onboarding1, "거주지와 알림 시간만 설정하고\n손쉽게 막차 알림을 받아보세요"),
            OnBoardingData(R.drawable.img_onboarding2, "알림처럼 간단하게 ON/OFF\n필요한 날에 켜주세요"),
            OnBoardingData(R.drawable.img_onboarding3, "시작하기 전에 위의 항목들을\n꼭 체크해주세요!")
        )
    }


}