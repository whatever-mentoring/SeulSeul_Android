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


    // LiveData for a single user
    private val _selectedUser = MutableLiveData<User>()
    val selectedUser: LiveData<User> get() = _selectedUser

    // POST
    val userIdPost = MutableLiveData("")
    val idPost = MutableLiveData("")
    val titlePost = MutableLiveData("")
    val bodyPost = MutableLiveData("")

    // DELETE
    val idDelete = MutableLiveData("")

    // PATCH
    val userIdPatch = MutableLiveData("")
    val idPatch = MutableLiveData("")
    val titlePatch = MutableLiveData("")
    val bodyPatch = MutableLiveData("")

    fun getUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.getUsers()
            if (response.isSuccessful) {
                Log.d("ViewModel", "Get users successful")
                // Save the first user to _selectedUser.
                response.body()?.firstOrNull()?.let { _selectedUser.postValue(it) }
            } else {
                Log.e("ViewModel", "Get users failed with code ${response.code()}")
            }
        }
    }

    // POST
    fun postUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.postUser(user)
            if (response.isSuccessful) {

                // You might want to add the new user to your local list here.
                // Or perhaps you need to refresh the entire list from the server.
            } else {
                // Handle error
            }
        }
    }

    fun deleteUser(id: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.deleteUser(id)
            if (response.isSuccessful) {
                // You might want to remove this user from your local list here.
                // Or perhaps you need to refresh the entire list from the server.
                getUsers()
            } else {
                // Handle error
            }

        }
    }

    fun patchUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.patchUser(user)
            if (response.isSuccessful) {

                // Or perhaps you need to refresh the entire list from the server.
                getUsers()

            } else {
                // Handle error
            }

        }
    }


}
