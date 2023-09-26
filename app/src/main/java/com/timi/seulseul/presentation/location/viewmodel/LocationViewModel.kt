package com.timi.seulseul.presentation.location.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.data.model.response.GetEndLocationData
import com.timi.seulseul.data.repository.GetEndLocationRepo
import com.timi.seulseul.data.repository.SetEndLocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getEndLocationRepo : GetEndLocationRepo,
    private val setEndLocationRepo: SetEndLocationRepo
) : ViewModel() {

    val endLocationData: MutableLiveData<List<GetEndLocationData>> = MutableLiveData()

    fun setEndLocation(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = setEndLocationRepo.setEndLocation(id)
                if(result.isSuccess){
                    Log.d("Success", "Successfully set end location")
                } else if(result.isFailure){
                    Log.e("Error", "Failed to set end location")
                }
            } catch (e: Exception){
                Log.e("Error", "Exception occurred while setting end location ${e.message}")
            }
        }
    }


    fun getEndLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = getEndLocationRepo.GetEndLocation()
                if(result.isSuccess) {
                    // Post the result directly if it's successful.
                    endLocationData.postValue(result.getOrNull()?.data)
                } else if (result.isFailure) {
                    Log.e("Error", "Failed to get end location")
                }
            } catch (e:Exception) {
                Log.e("Error", "Exception occurred while getting end location: ${e.message}")
            }
        }
    }

}