package com.timi.seulseul.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.data.model.Auth
import com.timi.seulseul.data.model.AuthData
import com.timi.seulseul.data.repository.AuthRepo
import com.timi.seulseul.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    private var _auth = MutableLiveData<Result<AuthData>>()
    var auth : LiveData<Result<AuthData>> = _auth

    fun postAuth() {
        // 가져온다.
        var uuid = prefs.getString("KEY_UUID", null)

        // 없다면 생성
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit().putString("KEY_UUID", uuid).apply()
        }

        // Auth 객체를 만들고 postAuth 함수에 전달합니다.
        viewModelScope.launch{
            _auth.value = authRepo.postAuth(Auth(uuid))
        }
    }

}