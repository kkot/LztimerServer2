package com.lztimer.server

import com.lztimer.server.config.ConfigProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties::class)
class LztimerServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(LztimerServerApplication::class.java, *args)
}
