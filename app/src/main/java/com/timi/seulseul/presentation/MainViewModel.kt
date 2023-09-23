package com.timi.seulseul.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.data.model.Auth
import com.timi.seulseul.data.model.AuthData
import com.timi.seulseul.data.repository.AuthRepo
import com.timi.seulseul.presentation.common.base.BaseActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

// Hilt가 ViewModel 객체를 생성하고 관리할 수 있게 해준다.
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    // Result<AuthData>를 _auth에 저장한다. 내부에서만 수정 가능
    private var _auth = MutableLiveData<Result<AuthData>>()
    // 외부에서 접근 가능한 auth는 읽기만 된다.
    var auth : LiveData<Result<AuthData>> = _auth

    private var _isDifferent : MutableLiveData<Boolean> = MutableLiveData(false)
    val isDifferent : LiveData<Boolean> = _isDifferent


    // postAuth함수는
    // uuid값을 넣은 Auth 객체를 만들고 authRepo.postAuth 함수에 전달한다.
    fun postAuth() {
        // uuid를 sp에서 가져온다.
        var uuid = prefs.getString("KEY_UUID", null)

        // 없다면 생성하고 sp에 저장한다.
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit().putString("KEY_UUID", uuid).apply()
        }

        viewModelScope.launch{
            // authRepo에서 postAuth를 호출해 반환된 결과 값을 _auth LiveData에 저장한다.
            _auth.value = authRepo.postAuth(Auth(uuid))
        }
    }

    // 현재 월, 일 받아오기
    fun getTodayDate() : MutableList<String> {
        val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale("ko", "KR"))

        var calendar = Calendar.getInstance()
        var date = Date()

        calendar.time = date

        var today = dateFormat.format(calendar.time) // 오늘

        calendar.add(Calendar.DATE, 1)
        var tomorrow = dateFormat.format(calendar.time) // 내일

        var month = today.substring(0, 2)
        month = deleteFrontZero(month)

        var day = today.substring(3, 5)
        day = deleteFrontZero(day)

        val hour = today.substring(6, 8)
        val minute = today.substring(9, 11)

        var tomorrowDay = tomorrow.substring(3, 5)
        tomorrowDay = deleteFrontZero(tomorrowDay)

        return mutableListOf(month, day, hour, minute, tomorrowDay)
    }

    // 월, 일 앞에 0 제거
    private fun deleteFrontZero(str : String) : String {
        var changedStr = str

        if (changedStr[0] == '0') {
            changedStr = changedStr[1].toString()
        }
        return changedStr
    }

    // 앱 처음 접근 -> refreshDay 저장
    fun saveRefreshDayFirst() {
        val date = getTodayDate()
        if (BaseActivity.prefs.getInt("refreshDay", 0) == 0) {
            BaseActivity.prefs.edit().putInt("refreshDay", date[4].toInt()).apply()
        }
    }

    // 알림 갱신 + refreshDay 갱신
    fun checkRefreshDay() {
        val date = getTodayDate()
        if (date[1].toInt() == BaseActivity.prefs.getInt("refreshDay", 0) && date[2].toInt() >= 2) {
            BaseActivity.prefs.edit().putBoolean("alarmOn", false).apply()
            BaseActivity.prefs.edit().putInt("refreshDay", date[4].toInt()).apply()
        }
    }


}