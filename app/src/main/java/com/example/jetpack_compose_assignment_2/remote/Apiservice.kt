package com.example.jetpack_compose_assignment_2.remote


import com.example.jetpack_compose_assignment_2.model.Todov
import retrofit2.http.GET

interface ApiService {
    @GET("todos")
    suspend fun getTodo(): List<Todov>
}
