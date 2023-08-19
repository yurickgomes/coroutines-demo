package com.example.coroutinesdemo.accounts

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountClient: AccountService
) {
    @GetMapping
    suspend fun findAll(): List<AccountModel> {
        return accountClient.findAll()
    }

    @GetMapping("/{id}/serial")
    suspend fun findByIdSerial(@PathVariable("id") id: String): AccountDto? {
        return accountClient.findByIdWithSerialCardDetails(id)
    }

    @GetMapping("/{id}/parallel")
    suspend fun findByIdParallel(@PathVariable("id") id: String): AccountDto? {
        return accountClient.findByIdWithParallelCardDetails(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createAccountInBackground(@RequestBody account: AccountDto) {
//        accountClient.createAccountInBackgroundUsingGlobalScope(account)
        accountClient.createAccountUsingWorker(account)
    }

    @PutMapping("/{id}")
    suspend fun updateAccount(@PathVariable("id") id: String, @RequestBody account: AccountDto): AccountDto {
        return accountClient.updateAccountInParallel(id, account)
    }
}
