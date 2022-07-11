package com.example.coroutinesdemo.accounts

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class RetrofitConfiguration {
    @Bean
    fun cardsClient(): CardsClient = Retrofit
        .Builder()
        .baseUrl("http://localhost:8082")
        .addConverterFactory(
            JacksonConverterFactory.create(jacksonObjectMapper())
        ).build()
        .create(CardsClient::class.java)
}
