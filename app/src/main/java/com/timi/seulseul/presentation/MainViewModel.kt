package com.timi.seulseul.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) : BaseViewModel() {

    private var _isDifferent : MutableLiveData<Boolean> = MutableLiveData(false)
    val isDifferent : LiveData<Boolean> get() = _isDifferent

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