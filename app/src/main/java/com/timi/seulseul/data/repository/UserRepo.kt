package com.timi.seulseul.data.repository

import com.timi.seulseul.data.api.ApiService
import com.timi.seulseul.data.model.User
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService : ApiService
) {

    suspend fun postUser(user: User): Response<User> {
        return apiService.postUser(user)
    }

    suspend fun getUser(id: String): Response<User> {
        return apiService.getUser(id)
    }

    suspend fun patchUser(user: User): Response<User> {
        return apiService.patchUser(user)
    }

    suspend fun deleteUser(id: String): Response<Unit> {
        return apiService.deleteUser(id)
    }

}
