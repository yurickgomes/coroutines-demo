package com.example.coroutinesdemo.cards

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cards")
class CardsController {
    @GetMapping
    suspend fun findAll() = listOf("card1", "card2", "card3", "card4", "card5")

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable("id") id: String): CardResponseDto = coroutineScope {
        delay(1000)
        CardResponseDto(id = "card1")
    }
}
