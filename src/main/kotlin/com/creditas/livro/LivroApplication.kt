package com.creditas.livro

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class LivroApplication

fun main(args: Array<String>) {
	runApplication<LivroApplication>(*args)
}
