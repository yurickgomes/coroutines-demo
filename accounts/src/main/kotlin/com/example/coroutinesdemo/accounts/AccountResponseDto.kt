package com.example.coroutinesdemo.accounts


data class AccountResponseDto(
    val id: String,
    val name: String,
    val cards: List<CardResponseDto>? = null
)
