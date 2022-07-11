package com.example.coroutinesdemo.accounts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Accounts

fun main(args: Array<String>) {
    runApplication<Accounts>(*args)
}
