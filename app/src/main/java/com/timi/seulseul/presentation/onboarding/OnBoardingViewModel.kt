package com.timi.seulseul.presentation.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.MainApplication
import com.timi.seulseul.R
import com.timi.seulseul.data.model.Auth
import com.timi.seulseul.data.model.OnBoardingData
import com.timi.seulseul.data.model.request.FcmTokenRequest
import com.timi.seulseul.data.repository.AuthRepo
import com.timi.seulseul.data.repository.FcmTokenRepo
import com.timi.seulseul.presentation.common.constants.KEY_UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val authRepo: AuthRepo
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

    fun postAuth() {
        // uuid를 sp에서 가져온다.
        var uuid = MainApplication.prefs.getString(KEY_UUID, null)

        // 없다면 생성하고 sp에 저장한다.
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            MainApplication.prefs.edit().putString(KEY_UUID, uuid).apply()
        }

        viewModelScope.launch{
            // authRepo에서 postAuth를 호출해 반환된 결과 값을 _auth LiveData에 저장한다.
            authRepo.postAuth(Auth(uuid))
        }
    }
}