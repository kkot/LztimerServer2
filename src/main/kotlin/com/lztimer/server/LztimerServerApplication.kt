package com.lztimer.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class LztimerServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(LztimerServerApplication::class.java, *args)
}
