package com.timi.seulseul.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.data.model.TodayTime
import com.timi.seulseul.data.model.LastSubwayArriveTime
import com.timi.seulseul.data.model.LastSubwayDepartTime
import com.timi.seulseul.data.model.DayInfo
import com.timi.seulseul.data.model.request.AlarmPatchRequest
import com.timi.seulseul.data.model.request.AlarmPostRequest
import com.timi.seulseul.data.repository.AlarmRepo
import com.timi.seulseul.data.repository.SubwayRouteRepo
import com.timi.seulseul.data.model.SubwayRouteItem
import com.timi.seulseul.presentation.common.constants.ALARM_ID
import com.timi.seulseul.presentation.common.constants.ALARM_ON
import com.timi.seulseul.presentation.common.constants.ALARM_TERM
import com.timi.seulseul.presentation.common.constants.ALARM_TIME
import com.timi.seulseul.presentation.common.constants.BASE_ROUTE_ID
import com.timi.seulseul.presentation.common.constants.IS_NOT_FIRST
import com.timi.seulseul.presentation.common.constants.POINT_ALARM_OFF
import com.timi.seulseul.presentation.common.constants.REFRESH
import com.timi.seulseul.presentation.common.constants.TODAY
import com.timi.seulseul.presentation.common.constants.TODAY_ALARM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val alarmRepo : AlarmRepo,
    private val subwayRouteRepo: SubwayRouteRepo
) : ViewModel() {
    private var _subwayData : MutableLiveData<MutableList<SubwayRouteItem>> = MutableLiveData()
    var subwayData : LiveData<MutableList<SubwayRouteItem>> = _subwayData

    private var _showRoute : MutableLiveData<Boolean> = MutableLiveData(false)
    var showRoute : LiveData<Boolean> = _showRoute

    private var _todayAlarmOff : MutableLiveData<Boolean> = MutableLiveData(false)
    var todayAlarmOff : LiveData<Boolean> = _todayAlarmOff

    fun setAlarm() {
        if (!prefs.getBoolean(IS_NOT_FIRST, false)) {
            postAlarm()
        } else {
            patchAlarm()
        }
    }

    private fun postAlarm() {
        val baseRouteId = prefs.getLong(BASE_ROUTE_ID, 0)
        val alarmTime = prefs.getInt(ALARM_TIME, 0)
        val alarmTerm = prefs.getInt(ALARM_TERM, 0)

        viewModelScope.launch {
            val response = alarmRepo.postAlarm(AlarmPostRequest(baseRouteId, alarmTime, alarmTerm))
            response.onSuccess {
                prefs.edit().apply {
                    putInt(ALARM_ID, it.id)
                    putBoolean(IS_NOT_FIRST, true)
                }.apply()
            }
        }
    }

    private fun patchAlarm() {
        val baseRouteId = prefs.getLong(BASE_ROUTE_ID, 0)
        val alarmTime = prefs.getInt(ALARM_TIME, 0)
        val alarmTerm = prefs.getInt(ALARM_TERM, 0)
        val alarmId = prefs.getInt(ALARM_ID, 0)

        viewModelScope.launch {
            alarmRepo.patchAlarm(AlarmPatchRequest(alarmId, baseRouteId, alarmTime, alarmTerm))
        }
    }

    fun patchAlarmEnabled() {
        val alarmId = prefs.getInt(ALARM_ID, 0)

        viewModelScope.launch {
            alarmRepo.patchAlarmEnabled(alarmId)
        }
    }

    fun getSubwayRoute() : MutableList<SubwayRouteItem> {
        val items : MutableList<SubwayRouteItem> = mutableListOf()

        viewModelScope.launch {
            val response = subwayRouteRepo.getSubwayRoute()
            response.onSuccess {
                // 경로 RecyclerView 관련 데이터 items에 추가
                it.totalTimeSection.forEach { totalTime ->
                    items.add(SubwayRouteItem.TotalTimeSectionItem(totalTime.data))
                }

                it.bodyList.forEach { body ->
                    if (body.viewType == "bodyInfo") {
                        items.add(SubwayRouteItem.BodyItem(body.data))
                    } else {
                        items.add(SubwayRouteItem.TransferItem(body.data))
                    }
                }

                items.add(SubwayRouteItem.Footer)

                _subwayData.value = items
                Timber.d("mainViewModel: ${_subwayData.value}")

                // 막차 시간
                val lastSubwayDepartTime = it.bodyList.first().data.departTime
                val lastSubwayArriveTime = it.bodyList.last().data.arriveTime
                Timber.d("lastSubwayDepartTime: $lastSubwayDepartTime")
                Timber.d("lastSubwayArriveTime: $lastSubwayArriveTime")

                changeTimeStringToInt(lastSubwayDepartTime!!, lastSubwayArriveTime!!)
            }
        }
        return items
    }

    private fun changeTimeStringToInt(departTime : String, arriveTime : String) {
        // 알림 시간 변환
        val alarmTime = prefs.getInt(ALARM_TIME, 0)
        val alarmHour = (alarmTime / 60).toLong()
        val alarmMinute = (alarmTime % 60).toLong()
        Timber.d("alarmTime: ${alarmHour}:${alarmMinute}")

        // today 변환
        val today = prefs.getString(TODAY, "")
        val todayYear = today!!.substring(0, 4).toInt()
        val todayMonth = today.substring(5, 7).toInt()
        val todayDate = today.substring(8).toInt()
        val todayTime = TodayTime(todayYear, todayMonth, todayDate)

        // 막차 출발 시간 변환
        val lastSubwayDepartYear = todayTime.year
        val lastSubwayDepartMonth = todayTime.month
        var lastSubwayDepartDate = todayTime.date
        val lastSubwayDepartHour = departTime.substring(0, 2).toInt()
        val lastSubwayDepartMinute = departTime.substring(3).toInt()

        val lastSubwayDepartTime : LastSubwayDepartTime

        if (lastSubwayDepartHour == 24 || lastSubwayDepartHour == 0) {
            lastSubwayDepartDate += 1

            val checkPassNextMonth = passNextMonth(lastSubwayDepartYear, lastSubwayDepartMonth, lastSubwayDepartDate)
            lastSubwayDepartTime = LastSubwayDepartTime(checkPassNextMonth.year, checkPassNextMonth.month, checkPassNextMonth.date, lastSubwayDepartHour, lastSubwayDepartMinute)

        } else {
            lastSubwayDepartTime = LastSubwayDepartTime(lastSubwayDepartYear, lastSubwayDepartMonth, lastSubwayDepartDate, lastSubwayDepartHour, lastSubwayDepartMinute)
        }
        Timber.d("막차출발시간: ${lastSubwayDepartTime.year} / ${lastSubwayDepartTime.month} / ${lastSubwayDepartTime.date} / ${lastSubwayDepartTime.hour} / ${lastSubwayDepartTime.minute}")



        // 막차 도착 시간 변환
        val lastSubwayArriveYear = todayTime.year
        val lastSubwayArriveMonth = todayTime.month
        var lastSubwayArriveDate = todayTime.date
        val lastSubwayArriveHour = arriveTime.substring(0, 2).toInt()
        val lastSubwayArriveMinute = arriveTime.substring(3).toInt()

        val lastSubwayArriveTime : LastSubwayArriveTime

        if (lastSubwayArriveHour == 24 || lastSubwayArriveHour == 0) {
            lastSubwayArriveDate += 1

            val checkPassNextMonth = passNextMonth(lastSubwayArriveYear, lastSubwayArriveMonth, lastSubwayArriveDate)
            lastSubwayArriveTime = LastSubwayArriveTime(checkPassNextMonth.year, checkPassNextMonth.month, checkPassNextMonth.date, lastSubwayArriveHour, lastSubwayArriveMinute)

        } else {
            lastSubwayArriveTime = LastSubwayArriveTime(lastSubwayArriveYear, lastSubwayArriveMonth, lastSubwayArriveDate, lastSubwayArriveHour, lastSubwayArriveMinute)
        }
        Timber.d("막차도착시간: ${lastSubwayArriveTime.year} / ${lastSubwayArriveTime.month} / ${lastSubwayArriveTime.date} / ${lastSubwayArriveTime.hour} / ${lastSubwayArriveTime.minute}")



        // 알림 시간, 막차 출발 시간 비교 -> '경로 보여줄 시간' 계산
        val showRouteTime = calShowRouteTime(alarmHour, alarmMinute, lastSubwayDepartTime)
        Timber.d("showRouteTime: $showRouteTime")

        // 경로 보여줄 시간, 현재 시간 비교 후 visibility 여부 체크
        checkRouteVisibility(todayTime, showRouteTime, lastSubwayDepartTime, lastSubwayArriveTime)
    }

    private fun calShowRouteTime(alarmHour: Long, alarmMinute: Long, lastSubwayDepartTime: LastSubwayDepartTime) : LocalTime {
        var departHour = lastSubwayDepartTime.hour
        if (departHour == 24) departHour = 0
        val departMinute = lastSubwayDepartTime.minute

        val time = LocalTime.of(departHour, departMinute)
        val duration = Duration.ofHours(alarmHour).plusMinutes(alarmMinute)

        return time.minus(duration)
    }

    private fun checkRouteVisibility(todayTime : TodayTime, showRouteTime : LocalTime, lastSubwayDepartTime: LastSubwayDepartTime, lastSubwayArriveTime : LastSubwayArriveTime) {
        _todayAlarmOff.value = false

        val currentTime = getDayInfo()
        val current = LocalDateTime.of(currentTime.year, currentTime.month, currentTime.date, currentTime.hour, currentTime.minute)


        val showRouteYear = todayTime.year
        val showRouteMonth = todayTime.month
        var showRouteDate = todayTime.date
        val showRouteHour = showRouteTime.hour
        val showRouteMinute = showRouteTime.minute

        val showRoute : LocalDateTime

        if (showRouteHour == 24 || showRouteHour == 0) {
            showRouteDate += 1

            val checkPassNextMonth = passNextMonth(showRouteYear, showRouteMonth, showRouteDate)
            showRoute = LocalDateTime.of(checkPassNextMonth.year, checkPassNextMonth.month, checkPassNextMonth.date, showRouteHour, showRouteMinute)

        } else {
            showRoute = LocalDateTime.of(showRouteYear, showRouteMonth, showRouteDate, showRouteHour, showRouteMinute)
        }
        Timber.d("경로보여지는시간: ${showRoute.year} / ${showRoute.month} / ${showRoute.dayOfMonth} / ${showRoute.hour} / ${showRoute.minute}")


        val subwayDepartTime = LocalDateTime.of(lastSubwayDepartTime.year, lastSubwayDepartTime.month, lastSubwayDepartTime.date, lastSubwayDepartTime.hour, lastSubwayDepartTime.minute)
        Timber.d("막차출발시간-1: ${subwayDepartTime.year} / ${subwayDepartTime.month} / ${subwayDepartTime.dayOfMonth} / ${subwayDepartTime.hour} / ${subwayDepartTime.minute}")


        // 경로 보여줄 시간
        if (current.isAfter(showRoute) || current.isEqual(showRoute)) {
            Timber.d("checkRouteVisibility: ${current} / ${showRoute}")

            _showRoute.value = true

        } else {
            Timber.d("OutOfRangeTime - checkRouteVisibility: ${current} / ${showRoute} ")
        }


        // 알림 초기화 시간
        if (current.isAfter(subwayDepartTime) || current.isEqual(subwayDepartTime)) {
            Timber.d("checkRouteVisibility: ${current} / ${subwayDepartTime}")

            if (prefs.getBoolean(POINT_ALARM_OFF, true)) {

                // 여기서 오늘 설정한 알림 종료
                if (prefs.getBoolean(ALARM_ON, false)) {
                    prefs.edit().putBoolean(ALARM_ON, false).apply() // 알림 off
                    patchAlarmEnabled()
                }

                prefs.edit().putBoolean(POINT_ALARM_OFF, false).apply()
                prefs.edit().putBoolean(TODAY_ALARM, false).apply() // 원래는 알림 on 할 때마다 true 인데 -> 여기서 막차 show시간 됐을 때 알림 off하기 위해 -> false

                _todayAlarmOff.value = true // 막차 show시간 되면 -> 설정한 알림 off (얘도 한번만 true)
            }

            calHideRouteTime(todayTime, current, lastSubwayArriveTime) // 현재 시간, 막차 도착

        } else {
            Timber.d("OutOfRangeTime - checkAlarmReset: ${current} / ${subwayDepartTime} ")
        }
    }

    private fun calHideRouteTime(todayTime: TodayTime, current : LocalDateTime, lastSubwayArriveTime: LastSubwayArriveTime) {
        val arriveYear = todayTime.year
        val arriveMonth = todayTime.month
        var arriveDate = todayTime.date
        var arriveHour = lastSubwayArriveTime.hour
        val arriveMinute = lastSubwayArriveTime.minute

        val hideRoute : LocalDateTime

        when(arriveHour) {
            24 -> {
                arriveHour = 0
                arriveDate += 1

                val checkPassNextMonth = passNextMonth(arriveYear, arriveMonth, arriveDate)
                hideRoute = LocalDateTime.of(checkPassNextMonth.year, checkPassNextMonth.month, checkPassNextMonth.date, arriveHour, arriveMinute)
            }

            25 -> {
                arriveHour = 1
                arriveDate += 1

                val checkPassNextMonth = passNextMonth(arriveYear, arriveMonth, arriveDate)
                hideRoute = LocalDateTime.of(checkPassNextMonth.year, checkPassNextMonth.month, checkPassNextMonth.date, arriveHour, arriveMinute)
            }

            else -> {
                hideRoute = LocalDateTime.of(arriveYear, arriveMonth, arriveDate, arriveHour, arriveMinute)
            }
        }

        if (current.isAfter(hideRoute) || current.isEqual(hideRoute)) {
            Timber.d("calHideRouteTime : ${current} / ${hideRoute}")

            _showRoute.value = false
            prefs.edit().remove(POINT_ALARM_OFF).apply() // 일회용으로 삭제
            prefs.edit().remove(TODAY).apply() // 다음 알림 설정하는 날 받아오기 위해서 삭제
            prefs.edit().putBoolean(REFRESH, true).apply() // 갱신 되었음 표시

        } else {
            Timber.d("OutOfRangeTime - calHideRouteTime: ${current} / ${hideRoute} ")
        }
    }

    // 현재 월,일 받아오기
    fun getDayInfo() : DayInfo {
        val time = LocalDateTime.now()
        val year = time.year
        val month = time.monthValue
        val date = time.dayOfMonth
        val hour = time.hour
        val minute = time.minute

        return DayInfo(year, month, date, hour, minute)
    }

    // 앱 처음 접근 -> today 저장
    fun saveToday() {
        val dayInfo = getDayInfo()
        val formattedMonth = String.format("%02d", dayInfo.month)
        val formattedDate = String.format("%02d", dayInfo.date)

        val today = "${dayInfo.year}-${formattedMonth}-${formattedDate}"

        if (prefs.getString(TODAY, "") == "") {
            prefs.edit().putString(TODAY, today).apply()
        }
    }

    private fun passNextMonth(year: Int, month: Int, day:Int) : TodayTime {
        var date : LocalDate

        try {
            date = LocalDate.of(year, month, day)

        } catch (e: Exception) {
            // 주어진 날짜가 유효하지 않을 경우, 다음 달의 첫 날로 설정
            date = LocalDate.of(year, month, 1).plusMonths(1)
        }

        val nYear = date.toString().substring(0, 4).toInt()
        val nMonth = date.toString().substring(5, 7).toInt()
        val nDate = date.toString().substring(8).toInt()

        return TodayTime(nYear, nMonth, nDate)
    }
}