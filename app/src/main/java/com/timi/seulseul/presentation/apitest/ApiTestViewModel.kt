package com.timi.seulseul.presentation.apitest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.data.model.User
import com.timi.seulseul.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiTestViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val _userLiveData = MutableLiveData<User?>()
    val userLiveData: LiveData<User?> get() = _userLiveData


//    init {
//        getUser("1")
//    }

    // GET
    fun getUser(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.getUser(id)
            if (response.isSuccessful) {
                Log.d("ViewModel", "Get User Successful")
                _userLiveData.postValue(response.body())
            } else {
                // Handle the error.
                Log.d("ViewModel", "Get User failed with code ${response.code()}")
            }
        }
    }

    // POST
    fun postUser(userId: String, id: String, title: String, body: String) {
        val newUser = User(userId = userId, id = id, title = title, body = body)
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.postUser(newUser)
            if (response.isSuccessful) {
                Log.d("ViewModel", "Post User Successful")
                _userLiveData.postValue(response.body())
            } else {
                // Handle the error.
                Log.d("ViewModel", "Post User failed with code ${response.code()}")
            }
        }
    }

    // PATCH
    fun patchUser(userId: String, id: String, title: String, body: String) {
        val updatedUser = User(userId = userId, id = id, title = title, body = body)
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.patchUser(updatedUser)
            if (response.isSuccessful) {
                Log.d("ViewModel", "Patch User Successful")
                _userLiveData.postValue(response.body())
            } else {
                // Handle the error.
                Log.d("ViewModel", "Patch User failed with code ${response.code()}")
            }
        }
    }

    // DELETE
    fun deleteUser(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.deleteUser(id)
            if (response.isSuccessful) {
                Log.d("ViewModel", "Delete User Successful")
                _userLiveData.postValue(null)
            } else {
                // Handle the error.
                Log.d("ViewModel", "Delete User failed with code ${response.code()}")
            }
        }
    }

}