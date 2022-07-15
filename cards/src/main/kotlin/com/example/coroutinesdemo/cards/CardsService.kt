package com.example.coroutinesdemo.cards

import kotlinx.coroutines.delay
import org.springframework.stereotype.Service

@Service
class CardsService {
    private val cardsMap = HashMap<String, String>()

    init {
        val cardsList = listOf(
            "card_1", "card_2", "card_3", "card_4", "card_5", "card_6", "card_7", "card_8", "card_9", "card_10"
        )
        cardsMap.putAll(cardsList.associateWith { it })
    }

    suspend fun findById(id: String): String? {
        delay(500)
        return cardsMap[id]
    }

    suspend fun findAll(): List<String>? {
        delay(600)
        return cardsMap.toList().map { it.first }
    }

    suspend fun save(id: String): String {
        delay(500)
        cardsMap[id] = id
        return id
    }
}
