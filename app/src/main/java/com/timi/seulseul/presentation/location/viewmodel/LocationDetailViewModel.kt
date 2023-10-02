package com.timi.seulseul.presentation.location.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.data.model.request.EndLocationRequest
import com.timi.seulseul.data.model.response.EndLocationData
import com.timi.seulseul.data.repository.EndLocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    private val endLocationRepo: EndLocationRepo
) : ViewModel() {

    lateinit var roadAddr: String
    lateinit var jibunAddr: String
    lateinit var locationRequest: EndLocationRequest

    val endLocationResponse : MutableLiveData<Result<EndLocationData>> = MutableLiveData()

    fun setAddress(roadAddr: String, jibunAddr: String) {
        this.roadAddr = roadAddr
        this.jibunAddr = jibunAddr

        locationRequest = EndLocationRequest(
            endX = 0.0,
            endY = 0.0,
            endNickName = "",
            roadNameAddress = roadAddr,
            jibunAddress = jibunAddr
        )
    }

    fun postEndLocation(request: EndLocationRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = endLocationRepo.postEndLocation(request)
            endLocationResponse.postValue(response)
        }
    }


}