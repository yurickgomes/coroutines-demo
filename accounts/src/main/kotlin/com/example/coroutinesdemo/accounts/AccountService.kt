package com.example.coroutinesdemo.accounts

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Service
class AccountService(
    private val cardClient: CardClient
) {
    private val accountsMap = HashMap<String, AccountModel>()
    private val worker = ThreadPoolExecutor(
        1,
        4,
        60,
        TimeUnit.SECONDS,
        LinkedBlockingQueue()
    )

    init {
        worker.allowCoreThreadTimeOut(true)
        val account = AccountModel("account1", "Bruce Wayne", listOf("card_1", "card_2"))
        accountsMap[account.id] = account
    }

    suspend fun findAll(): List<AccountModel> {
        return accountsMap.map { it.value }.toList()
    }

    suspend fun findByIdWithSerialCardDetails(id: String): AccountDto? {
        val account = accountsMap[id] ?: return null
        // Let's offload blocking I/O operation to the elastic Dispatchers.IO
        // https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-i-o.html
        return withContext(Dispatchers.IO) {
            Thread.sleep(200L) // Blocking operation
            // coroutines run sequentially by default
            val cards = account.cards?.map { card ->
                cardClient.findByCardId(card)!!
            }

            AccountDto(
                id = account.id,
                name = account.name,
                cards = cards
            )
        }

    }

    suspend fun findByIdWithParallelCardDetails(id: String): AccountDto? = coroutineScope {
        val account = accountsMap[id] ?: return@coroutineScope null
        // request cards details in parallel
        val cardsDeferred = account.cards?.map { card -> async { cardClient.findByCardId(card)!! } }
        val cards = cardsDeferred?.awaitAll()

        return@coroutineScope AccountDto(
            id = account.id,
            name = account.name,
            cards = cards
        )
    }

    suspend fun updateAccountInParallel(id: String, accountDto: AccountDto): AccountDto =
        coroutineScope {
            accountsMap[id] ?: throw RuntimeException("Account not found")
            // we don't really care for the result of cardClient.addNewCard, so let's use launch
            accountDto.cards?.forEach { card -> launch { cardClient.addNewCard(card) } }
            accountsMap[id] = accountDto.toAccountModel()

            return@coroutineScope accountDto.copy()
        }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun createAccountInBackgroundUsingGlobalScope(accountDto: AccountDto) {
        // not recommended, see https://elizarov.medium.com/the-reason-to-avoid-globalscope-835337445abc
        GlobalScope.launch {
            accountsMap[accountDto.id] = accountDto.toAccountModel()
            accountDto.cards?.forEach { card -> launch { cardClient.addNewCard(card) } }
        }
    }

    fun createAccountUsingWorker(accountDto: AccountDto) {
        accountsMap[accountDto.id] = accountDto.toAccountModel()
        accountDto.cards?.forEach { card ->
            worker.submit { // not really using the coroutines way, just a possible replacement for GlobalScope usage
                runBlocking {
                    cardClient.addNewCard(card)
                }
            }
        }
    }
}

fun AccountDto.toAccountModel() = AccountModel(id, name, cards?.map { it.id })
