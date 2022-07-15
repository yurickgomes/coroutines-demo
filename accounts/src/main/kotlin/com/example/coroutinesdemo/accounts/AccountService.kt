package com.example.coroutinesdemo.accounts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val cardClient: CardClient
) {
    private val accountsMap = HashMap<String, AccountModel>()

    init {
        val account = AccountModel("account1", "Bruce Wayne", listOf("card_1", "card_2"))
        accountsMap[account.id] = account
    }

    suspend fun findAll(): List<AccountModel> {
        delay(400)
        return accountsMap.map { it.value }.toList()
    }

    suspend fun findByIdWithSerialCardDetails(id: String): AccountResponseDto? {
        delay(100)
        val account = accountsMap[id] ?: return null
        val cards = withContext(Dispatchers.IO) {
            account.cards?.map { card -> cardClient.findByCardId(card)!! }
        }

        return AccountResponseDto(
            id = account.id,
            name = account.name,
            cards = cards
        )
    }

    suspend fun findByIdWithParallelCardDetails(id: String): AccountResponseDto? {
        delay(100)
        val account = accountsMap[id] ?: return null
        val cards = withContext(Dispatchers.IO) {
            val cardsDeferredList = account.cards?.map { card -> async { cardClient.findByCardId(card)!! } }
            cardsDeferredList?.awaitAll()
        }

        return AccountResponseDto(
            id = account.id,
            name = account.name,
            cards = cards
        )
    }
}
