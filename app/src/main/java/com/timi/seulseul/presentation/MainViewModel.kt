package com.timi.seulseul.presentation

import com.timi.seulseul.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) : BaseViewModel() {

    // 현재 월, 일 받아오기 + view 처리
    fun getMonthDay() : MutableList<String> {
        val currentTime = System.currentTimeMillis() // ms로 반환
        val date = Date(currentTime)
        val dateFormat = SimpleDateFormat("MM-dd E", Locale("ko", "KR"))
        val strDate = dateFormat.format(date)

        var month = strDate.substring(0, 2)
        month = deleteFrontZero(month)

        var day = strDate.substring(3, 5)
        day = deleteFrontZero(day)

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
}