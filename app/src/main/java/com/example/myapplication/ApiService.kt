package com.example.myapplication

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: String
    ): UserResponse
}