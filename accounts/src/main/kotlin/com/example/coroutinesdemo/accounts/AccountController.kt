package com.example.coroutinesdemo.accounts

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountClient: AccountService
) {
    @GetMapping("/")
    suspend fun findAll(): List<AccountModel> {
        return accountClient.findAll()
    }

    @GetMapping("/{id}/serial")
    suspend fun findByIdSerial(@PathVariable("id") id: String): AccountResponseDto? {
        return accountClient.findByIdWithSerialCardDetails(id)
    }

    @GetMapping("/{id}/parallel")
    suspend fun findByIdParallel(@PathVariable("id") id: String): AccountResponseDto? {
        return accountClient.findByIdWithParallelCardDetails(id)
    }
}
