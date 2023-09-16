package com.timi.seulseul.data.repository

import com.timi.seulseul.data.api.ApiService
import com.timi.seulseul.data.model.User
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService
) {

    //GET
    suspend fun getUsers(): Response<List<User>> {
        return apiService.getUsers()
    }

    //POST
    suspend fun postUser(user: User): Response<User> {
        return apiService.postUser(user)
    }


    //DELETE
    suspend fun deleteUser(id: String): Response<Unit> {
        return apiService.deleteUser(id)
    }

    //PATCH
    suspend fun patchUser(user: User): Response<User> {
        return apiService.patchUser(user)
    }


}
