package com.example.coroutinesdemo.accounts

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
        delay(100)
        val account = accountsMap[id] ?: return null
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

    suspend fun updateAccountParallelWithConfirmation(id: String, accountDto: AccountDto): AccountDto {
        delay(100)
        accountsMap[id] ?: throw RuntimeException("Account not found")
        withContext(Dispatchers.IO) {
            val newCardsDeferredList = accountDto.cards?.map { card -> async { cardClient.addNewCard(card) } }
            newCardsDeferredList?.awaitAll()
        }
        accountsMap[id] = accountDto.toAccountModel()

        return accountDto.copy()
    }

    suspend fun createAccountParallelFireAndForget(accountDto: AccountDto) {
        accountsMap[accountDto.id] = accountDto.toAccountModel()
//        A global CoroutineScope not bound to any job. Global scope is used to launch top-level coroutines which are
//        operating on the whole application lifetime and are not cancelled prematurely.
//        Active coroutines launched in GlobalScope do not keep the process alive. They are like daemon threads.
//        This is a delicate API. It is easy to accidentally create resource or memory leaks when GlobalScope is used.
//        A coroutine launched in GlobalScope is not subject to the principle of structured concurrency, so if it
//        hangs or gets delayed due to a problem (e.g. due to a slow network),
//        it will stay working and consuming resources.
//        We can do better!!
        GlobalScope.launch {
            accountDto.cards?.map { card -> launch { cardClient.addNewCard(card) } }
        }
    }
}

fun AccountDto.toAccountModel() = AccountModel(id, name, cards?.map { it.id })
