package com.timi.seulseul.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.MainApplication.Companion.prefs
import com.timi.seulseul.data.model.Auth
import com.timi.seulseul.data.model.AuthData
import com.timi.seulseul.data.model.DayInfo
import com.timi.seulseul.data.model.request.AlarmPatchRequest
import com.timi.seulseul.data.model.request.AlarmPostRequest
import com.timi.seulseul.data.repository.AlarmRepo
import com.timi.seulseul.data.repository.AuthRepo
import com.timi.seulseul.data.repository.SubwayRouteRepo
import com.timi.seulseul.presentation.common.base.BaseActivity
import com.timi.seulseul.presentation.main.adapter.SubwayRouteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

// Hilt가 ViewModel 객체를 생성하고 관리할 수 있게 해준다.
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val alarmRepo : AlarmRepo,
    private val subwayRouteRepo: SubwayRouteRepo
) : ViewModel() {

    // Result<AuthData>를 _auth에 저장한다. 내부에서만 수정 가능
    private var _auth = MutableLiveData<Result<AuthData>>()
    // 외부에서 접근 가능한 auth는 읽기만 된다.
    var auth : LiveData<Result<AuthData>> = _auth

    private var _subwayData : MutableLiveData<MutableList<SubwayRouteItem>> = MutableLiveData()
    var subwayData : LiveData<MutableList<SubwayRouteItem>> = _subwayData

    private var _showRoute : MutableLiveData<Boolean> = MutableLiveData(false)
    var showRoute : LiveData<Boolean> = _showRoute

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
                val lastSubwayTime = it.bodyList.last().data.departTime
                Timber.d("lastSubwayTime: $lastSubwayTime")

                // 경로 보여줄 시간
                getHaveToShowRouteTIme(lastSubwayTime!!)
            }
        }
        return items
    }

    private fun getHaveToShowRouteTIme(lastSubwayTime: String) {
        val alarmTime = prefs.getInt("alarmTime", 0)
        val alarmHour = alarmTime / 60
        val alarmMinute = alarmTime % 60
        Timber.d("alarmTime: ${alarmHour}:${alarmMinute}")

        val lastSubwayHour = lastSubwayTime.substring(0, 2).toInt()
        val lastSubwayMinute = lastSubwayTime.substring(3).toInt()

        // 막차 시간, 알림 시간 비교해서 경로 보여줄 시간 계산
        val timeToShowRoute = calculateLastSubwayTime(lastSubwayHour, lastSubwayMinute, alarmHour, alarmMinute)
        prefs.edit().putString("timeToShowRoute", timeToShowRoute).apply()
        Timber.d("getHaveToShowRouteTime: $timeToShowRoute")

        // 경로 보여줄 시간, 현재 시간 비교 후 visibility 여부 체크
        checkRouteVisibility(timeToShowRoute)
    }

    private fun calculateLastSubwayTime(lastSubwayHour: Int, lastSubwayMinute: Int, alarmHour: Int, alarmMinute: Int) : String {
        var lHour = lastSubwayHour
        var lMinute = lastSubwayMinute

        // 24시, 25시 고려
        if (lHour >= 24) {
            lHour -= 24
        }

        // 분 끼리 뺐을 때 음수인 경우 고려
        if (lMinute - alarmMinute < 0) {
            lHour -= 1
            lMinute += 60
        }

        // 시 끼리 뺐을 때 음수인 경우 고려
        if (lHour - alarmHour < 0) {
            lHour += 24
        }

        return "${lHour-alarmHour}:${lMinute-alarmMinute}"
    }

    private fun checkRouteVisibility(timeToShowRoute : String) {
        var routeHour = timeToShowRoute.substring(0, 2).toInt()
        val routeMinute = timeToShowRoute.substring(3).toInt()

        if (routeHour == 24) {
            routeHour = 0
        }
        Timber.d("checkRouteVisibility : ${routeHour}:${routeMinute}")

        val currentTime = getTodayDate()
        val currentHour = currentTime.hour
        val currentMinute = currentTime.minute
        Timber.d("currentTime : ${currentHour}:${currentMinute}")

        if (currentHour >= routeHour && currentMinute >= routeMinute) {
            _showRoute.value = true
        }
    }


    // 현재 월,일 받아오기
    fun getTodayDate() : DayInfo {
        val calendar = Calendar.getInstance()

        val month = calendar.get(Calendar.MONTH)+1
        val date = calendar.get(Calendar.DATE)
        val tomorrow = date+1

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return DayInfo(month, date, hour, minute, tomorrow)
    }

    // 앱 처음 접근 -> refreshDay 저장
    fun saveRefreshDayFirst() {
        val dayInfo = getTodayDate()
        if (BaseActivity.prefs.getInt("refreshDay", 0) == 0) {
            BaseActivity.prefs.edit().putInt("refreshDay", dayInfo.tomorrow).apply()
        }
    }

    // 알림 갱신 + refreshDay 갱신
    fun checkRefreshDay() {
        val dayInfo = getTodayDate()
        if (dayInfo.date == BaseActivity.prefs.getInt("refreshDay", 0) && dayInfo.hour >= 2) {
            BaseActivity.prefs.edit().putBoolean("alarmOn", false).apply()
            BaseActivity.prefs.edit().putInt("refreshDay", dayInfo.tomorrow).apply()
        }
    }
}