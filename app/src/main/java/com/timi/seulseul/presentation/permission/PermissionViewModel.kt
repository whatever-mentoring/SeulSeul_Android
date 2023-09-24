package com.timi.seulseul.presentation.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.data.model.request.FcmTokenRequest
import com.timi.seulseul.data.repository.FcmTokenRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val fcmTokenRepo: FcmTokenRepo
) : ViewModel() {

    fun postFcmToken(fcmToken : String) {
        viewModelScope.launch {
            fcmTokenRepo.postFcmToken(FcmTokenRequest(fcmToken))
        }
    }
}