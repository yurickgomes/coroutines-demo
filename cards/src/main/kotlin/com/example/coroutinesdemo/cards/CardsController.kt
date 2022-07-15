package com.example.coroutinesdemo.cards

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cards")
class CardsController(
    private val cardsService: CardsService
) {
    @GetMapping
    suspend fun findAll(): List<String>? {
        return cardsService.findAll()
    }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable("id") id: String): CardResponseDto? {
        val card = cardsService.findById(id) ?: return null

        return CardResponseDto(card)
    }

    @PostMapping("/{id}")
    suspend fun addNewCard(@PathVariable("id") id: String): CardResponseDto {
        return CardResponseDto(cardsService.save(id))
    }
}
