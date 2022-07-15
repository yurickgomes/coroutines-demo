package com.example.coroutinesdemo.cards

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
    suspend fun findById(@PathVariable("id") id: String): CardDto? {
        val card = cardsService.findById(id) ?: return null

        return CardDto(card)
    }

    @PostMapping
    suspend fun addNewCard(@RequestBody cardDto: CardDto): CardDto {
        return CardDto(cardsService.save(cardDto.id))
    }
}
