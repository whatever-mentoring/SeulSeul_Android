package com.timi.seulseul.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.data.model.Auth
import com.timi.seulseul.data.model.AuthData
import com.timi.seulseul.data.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
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
    val isDifferent : LiveData<Boolean>  = _isDifferent

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
        val currentTime = System.currentTimeMillis()
        val date = Date(currentTime)
        val dateFormat = SimpleDateFormat("MM-dd", Locale("ko", "KR"))
        val strDate = dateFormat.format(date)

        var month = strDate.substring(0, 2)
        month = deleteFrontZero(month)

        var day = strDate.substring(3, 5)
        day = deleteFrontZero(day)
        Timber.d("현재 날짜 : $month / $day")

        return mutableListOf(month, day)
    }

    // 월, 일 앞에 0 제거
    private fun deleteFrontZero(str : String) : String {
        var changedStr = str

        if (changedStr[0] == '0') {
            changedStr = changedStr[1].toString()
        }
        return changedStr
    }

    // 현재 날짜 체크
    fun checkDiffDay(currentMonth : String, currentDay : String) {
        var month = prefs.getString("currentMonth", "")
        var day = prefs.getString("currentDay", "")
        Timber.d("prefs 확인: $month / $day")

        if (month !=currentMonth || day != currentDay) {
            // 갖고 있는 데이터랑 현재 날짜 다름
            _isDifferent.value = true

            month = currentMonth
            day = currentDay

            // 현재 날짜로 다시 세팅
            prefs.edit().apply {
                putString("currentMonth", month)
                putString("currentDay", day)
            }.apply()
            Timber.d("prefs 갱신: $month / $day")

        } else {
            // 갖고 있는 데이터랑 현재 날짜 같음
            _isDifferent.value = false
        }
    }

}