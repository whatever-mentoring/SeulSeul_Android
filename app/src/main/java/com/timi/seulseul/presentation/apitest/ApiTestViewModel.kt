package com.timi.seulseul.presentation.apitest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timi.seulseul.data.model.User
import com.timi.seulseul.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiTestViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    //GET
    private var _userStateFlow = MutableStateFlow<List<User>>(emptyList())
    val userStateFlow: StateFlow<List<User>> get() = _userStateFlow


//    // ViewModel 처음 생성될 때 실행되게 하는 코드 -> 실행되자마자 api바로 받아와짐
//    // 이거 주석처리하면 HomeScreen에서
//    init {
//        fetchUsers()
//    }

    //GET
    fun getUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.getUsers()
            if (response.isSuccessful) {
                Log.d("ApiTestViewModel", "Get users successful")
                _userStateFlow.emit(response.body() ?: listOf())
            } else {
                Log.e("ApiTestViewModel", "Get users failed with code ${response.code()}")
            }
        }
    }

    //POST
    fun postUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.postUser(user)
            if (response.isSuccessful) {
                // 요청 성공 시 로직 처리
                Log.d("ApiTestViewModel", "User post successful")

                val updatedList = _userStateFlow.value.toMutableList()
                response.body()?.let { updatedList.add(it) }
                _userStateFlow.emit(updatedList)

            } else {
                // 요청 실패 시 로직 처리
                Log.d("ApiTestViewModel", "User post failed with code ${response.code()}")
            }
        }
    }

    //DELETE
    fun deleteUser(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.deleteUser(id)
            if (response.isSuccessful) {
                Log.d("ApiTestViewModel", "User delete successful")
                val updatedList = _userStateFlow.value.filterNot { it.id == id }
                _userStateFlow.emit(updatedList)
            } else {
                Log.d("ApiTestViewModel", "User delete failed with code ${response.code()}")
            }
        }
    }

    //PATCH
    fun patchUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.patchUser(user)
            if (response.isSuccessful) {
                Log.d("ApiTestViewModel", "Patch user successful")

                response.body()?.let { newUser ->
                    val updatedList = _userStateFlow.value.map { existingUser ->
                        if (existingUser.id == newUser.id) { // Assuming 'id' is a property of 'user'
                            newUser // If the user is the one we patched, replace it with the new user
                        } else {
                            existingUser // Otherwise, keep the original user
                        }
                    }
                    _userStateFlow.emit(updatedList)
                }

            } else {
                Log.e("ApiTestViewModel", "Patch user failed with code ${response.code()}")
            }
        }
    }
}
