package com.example.coroutinesdemo.accounts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountsController(
    private val cardsClient: CardsClient
) {
    @GetMapping
    suspend fun findAll(): List<String>? {
        return withContext(Dispatchers.IO) {
            cardsClient.findAll()
        }
    }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable("id") id: String): String? {
        return withContext(Dispatchers.IO) {
            cardsClient.findByCardId(id)?.id
        }
    }
}
