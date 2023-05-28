package com.example.coroutinesdemo.accounts

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    suspend fun findByIdWithSerialCardDetails(id: String): AccountDto? {
        delay(100)
        val account = accountsMap[id] ?: return null
        val cards = withContext(Dispatchers.IO) {
            account.cards?.map { card -> cardClient.findByCardId(card)!! }
        }

        return AccountDto(
            id = account.id,
            name = account.name,
            cards = cards
        )
    }

    suspend fun findByIdWithParallelCardDetails(id: String): AccountDto? {
        val account = accountsMap[id] ?: return null
        delay(100)
        val cards = withContext(Dispatchers.IO) {
            val cardsDeferredList = account.cards?.map { card -> async { cardClient.findByCardId(card)!! } }
            cardsDeferredList?.awaitAll()
        }

        return AccountDto(
            id = account.id,
            name = account.name,
            cards = cards
        )
    }

    suspend fun updateAccountInParallelWithConfirmation(id: String, accountDto: AccountDto): AccountDto {
        accountsMap[id] ?: throw RuntimeException("Account not found")
        delay(300)
        withContext(Dispatchers.IO) {
            val newCardsDeferredList = accountDto.cards?.map { card -> async { cardClient.addNewCard(card) } }
            newCardsDeferredList?.awaitAll()
        }
        accountsMap[id] = accountDto.toAccountModel()

        return accountDto.copy()
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun createAccountInBackground(accountDto: AccountDto) {
        GlobalScope.launch {
            accountsMap[accountDto.id] = accountDto.toAccountModel()
            accountDto.cards?.forEach { card -> launch { cardClient.addNewCard(card) } }
        }
    }
}

fun AccountDto.toAccountModel() = AccountModel(id, name, cards?.map { it.id })
