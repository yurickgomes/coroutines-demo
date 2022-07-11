package com.example.coroutinesdemo.accounts

import retrofit2.http.GET
import retrofit2.http.Path

interface CardsClient {
    @GET("/cards")
    suspend fun findAll(): List<String>?

    @GET("/cards/{id}")
    suspend fun findByCardId(@Path("id") id: String): CardDto?
}
