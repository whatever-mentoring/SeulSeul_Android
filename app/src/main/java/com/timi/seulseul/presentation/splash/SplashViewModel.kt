package com.timi.seulseul.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timi.seulseul.data.repository.SPRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val spRepo: SPRepo,
) : ViewModel() {

    private var _spLiveData = MutableLiveData(spRepo.checkOnBoarding())
    val spLiveData: LiveData<Boolean> = _spLiveData


}