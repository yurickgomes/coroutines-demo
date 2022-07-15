package com.example.coroutinesdemo.accounts

data class AccountModel(
    val id: String,
    val name: String,
    val cards: List<String>? = null,
)
