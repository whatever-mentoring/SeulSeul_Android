package com.timi.seulseul.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.data.model.Auth
import com.timi.seulseul.data.model.AuthData
import com.timi.seulseul.data.model.TodayTime
import com.timi.seulseul.data.model.LastSubwayArriveTime
import com.timi.seulseul.data.model.LastSubwayDepartTime
import com.timi.seulseul.data.model.RealDayInfo
import com.timi.seulseul.data.model.request.AlarmPatchRequest
import com.timi.seulseul.data.model.request.AlarmPostRequest
import com.timi.seulseul.data.repository.AlarmRepo
import com.timi.seulseul.data.repository.AuthRepo
import com.timi.seulseul.data.repository.SubwayRouteRepo
import com.timi.seulseul.presentation.main.adapter.SubwayRouteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

// Hilt가 ViewModel 객체를 생성하고 관리할 수 있게 해준다.
@HiltViewModel
class MainViewModel @Inject constructor(
    private val alarmRepo : AlarmRepo,
    private val subwayRouteRepo: SubwayRouteRepo
) : ViewModel() {
    private var _subwayData : MutableLiveData<MutableList<SubwayRouteItem>> = MutableLiveData()
    var subwayData : LiveData<MutableList<SubwayRouteItem>> = _subwayData

    private var _showRoute : MutableLiveData<Boolean> = MutableLiveData(false)
    var showRoute : LiveData<Boolean> = _showRoute

    fun setAlarm() {
        if (!prefs.getBoolean("isNotFirst", false)) {
            postAlarm()
        } else {
            patchAlarm()
        }
    }

    private fun postAlarm() {
        val alarmTime = prefs.getInt("alarmTime", 0)
        val alarmTerm = prefs.getInt("alarmTerm", 0)

        // TODO : 추후에 base_route_id 불러와야 함 (현재는 고정값)
        viewModelScope.launch {
            val response = alarmRepo.postAlarm(AlarmPostRequest(1, alarmTime, alarmTerm))
            response.onSuccess {
                prefs.edit().apply {
                    putInt("alarmId", it.id)
                    putBoolean("isNotFirst", true)
                }.apply()
            }
        }
    }

    private fun patchAlarm() {
        val alarmTime = prefs.getInt("alarmTime", 0)
        val alarmTerm = prefs.getInt("alarmTerm", 0)
        val alarmId = prefs.getInt("alarmId", 0)

        // TODO : 추후에 base_route_id 불러와야 함 (현재는 고정값)
        viewModelScope.launch {
            alarmRepo.patchAlarm(AlarmPatchRequest(alarmId, 1, alarmTime, alarmTerm))
        }
    }

    fun patchAlarmEnabled() {
        val alarmId = prefs.getInt("alarmId", 0)

        viewModelScope.launch {
            alarmRepo.patchAlarmEnabled(alarmId)
        }
    }

    fun getSubwayRoute() : MutableList<SubwayRouteItem> {
        val items : MutableList<SubwayRouteItem> = mutableListOf()

        viewModelScope.launch {
            val response = subwayRouteRepo.getSubwayRoute()
            response.onSuccess {
                // 경로 RecyclerView 관련 서 items에 데이터 추가
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
                val lastSubwayDepartTime = it.bodyList.last().data.departTime
                val lastSubwayArriveTime = it.bodyList.last().data.arriveTime
                Timber.d("lastSubwayDepartTime: $lastSubwayDepartTime")
                Timber.d("lastSubwayArriveTime: $lastSubwayArriveTime")

                changeTimeStringToInt(lastSubwayDepartTime!!, lastSubwayArriveTime!!)
            }
        }
        return items
    }

    fun changeTimeStringToInt(departTime : String, arriveTime : String) {
        // 알림 시간 변환
        val alarmTime = prefs.getInt("alarmTime", 0)
        val alarmHour = (alarmTime / 60).toLong()
        val alarmMinute = (alarmTime % 60).toLong()
        Timber.d("alarmTime: ${alarmHour}:${alarmMinute}")

        // today 변환
        val today = prefs.getString("today", "")
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


        // 막차 도착 시간 변환
        val lastSubwayArriveYear = todayTime.year
        val lastSubwayArriveMonth = todayTime.month
        var lastSubwayArriveDate = todayTime.date
        val lastSubwayArriveHour = arriveTime.substring(0, 2).toInt()
        val lastSubwayArriveMinute = arriveTime.substring(3).toInt()

        val lastSubwayArriveTime : LastSubwayArriveTime

        if (lastSubwayDepartHour == 24 || lastSubwayDepartHour == 0) {
            lastSubwayArriveDate += 1

            val checkPassNextMonth = passNextMonth(lastSubwayArriveYear, lastSubwayArriveMonth, lastSubwayArriveDate)
            lastSubwayArriveTime = LastSubwayArriveTime(checkPassNextMonth.year, checkPassNextMonth.month, checkPassNextMonth.date, lastSubwayDepartHour, lastSubwayDepartMinute)

        } else {
            lastSubwayArriveTime = LastSubwayArriveTime(lastSubwayArriveYear, lastSubwayArriveMonth, lastSubwayArriveDate, lastSubwayArriveHour, lastSubwayArriveMinute)
        }


        // 알림 시간, 막차 출발 시간 비교 -> '경로 보여줄 시간' 계산
        val showRouteTime = calShowRouteTime(alarmHour, alarmMinute, lastSubwayDepartTime)
        Timber.d("showRouteTime: $showRouteTime")

        // 경로 보여줄 시간, 현재 시간 비교 후 visibility 여부 체크
        checkRouteVisibility(todayTime, showRouteTime, lastSubwayArriveTime)
    }

    private fun calShowRouteTime(alarmHour: Long, alarmMinute: Long, lastSubwayDepartTime: LastSubwayDepartTime) : LocalTime {
        var departHour = lastSubwayDepartTime.hour
        if (departHour == 24) departHour = 0
        val departMinute = lastSubwayDepartTime.minute

        val time = LocalTime.of(departHour, departMinute)
        val duration = Duration.ofHours(alarmHour).plusMinutes(alarmMinute)

        return time.minus(duration)
    }

    private fun checkRouteVisibility(todayTime : TodayTime, showRouteTime : LocalTime, lastSubwayArriveTime : LastSubwayArriveTime) {
        val currentTime = getDayInfo()

        val showRouteYear = todayTime.year
        val showRouteMonth = todayTime.month
        var showRouteDate = todayTime.date
        val showRouteHour = showRouteTime.hour
        val showRouteMinute = showRouteTime.minute

        val current = LocalDateTime.of(currentTime.year, currentTime.month, currentTime.date, currentTime.hour, currentTime.minute)
        val showRoute : LocalDateTime

        if (showRouteHour == 24 || showRouteHour == 0) {
            showRouteDate += 1

            val checkPassNextMonth = passNextMonth(showRouteYear, showRouteMonth, showRouteDate)
            showRoute = LocalDateTime.of(checkPassNextMonth.year, checkPassNextMonth.month, checkPassNextMonth.date, showRouteHour, showRouteMinute)

        } else {
            showRoute = LocalDateTime.of(showRouteYear, showRouteMonth, showRouteDate, showRouteHour, showRouteMinute)
        }

        if (current.isAfter(showRoute) || current.isEqual(showRoute)) {
            Timber.d("checkRouteVisibility: ${current} / ${showRoute}")

            _showRoute.value = true
            // TODO : 여기에 알림 Off 로직 넣기
            calHideRouteTime(todayTime, current, lastSubwayArriveTime) // 현재 시간, 막차 도착 시간
        } else {
            Timber.d("OutOfRangeTime - checkRouteVisibility: ${current} / ${showRoute} ")
        }
    }

    private fun calHideRouteTime(todayTime: TodayTime, current : LocalDateTime, lastSubwayArriveTime: LastSubwayArriveTime) {
        var arriveYear = todayTime.year
        var arriveMonth = todayTime.month
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
            prefs.edit().putBoolean("todayAlarm", false).apply()
            prefs.edit().putBoolean("alarmOn", false).apply() // 변경 필요
            prefs.edit().remove("today").apply()

        } else {
            Timber.d("OutOfRangeTime - calHideRouteTime: ${current} / ${hideRoute} ")
        }
    }

    // 현재 월,일 받아오기
    fun getDayInfo() : RealDayInfo {
        val time = LocalDateTime.now()
        val year = time.year
        val month = time.monthValue
        val date = time.dayOfMonth
        val hour = time.hour
        val minute = time.minute

        return RealDayInfo(year, month, date, hour, minute)
    }

    // 앱 처음 접근 -> today 저장
    fun saveToday() {
        val dayInfo = getDayInfo()
        val formattedMonth = String.format("%02d", dayInfo.month)
        val formattedDate = String.format("%02d", dayInfo.date)

        val today = "${dayInfo.year}-${formattedMonth}-${formattedDate}"

        if (prefs.getString("today", "") == "") {
            prefs.edit().putString("today", today).apply()
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