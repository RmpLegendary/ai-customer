package com.ai.customer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CustomerApplication

fun main(args: Array<String>) {
	runApplication<CustomerApplication>(*args)
}