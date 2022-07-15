package com.example.coroutinesdemo.accounts


data class AccountDto(
    val id: String,
    val name: String,
    val cards: List<CardDto>? = null
)
