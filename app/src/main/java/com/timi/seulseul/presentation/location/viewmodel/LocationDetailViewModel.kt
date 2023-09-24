package com.timi.seulseul.presentation.location.viewmodel

import androidx.lifecycle.ViewModel
import com.timi.seulseul.data.model.request.EndLocationRequest
import com.timi.seulseul.data.repository.EndLocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    private val endLocationRepo : EndLocationRepo
) : ViewModel() {

    var roadAddr : String? = null
    var jibunAddr :String? = null

    var locationRequest : EndLocationRequest? = null

    fun setAddress(roadAddr : String?, jibunAddr : String?) {
        this.roadAddr = roadAddr
        this.jibunAddr = jibunAddr

        locationRequest = EndLocationRequest(
            endX = 0.0,
            endY = 0.0,
            endNickName="",
            roadNameAddress=roadAddr!!,
            jibunAddress=jibunAddr!!
        )
    }


}