package com.creditas.livro.exception

class BadRequestException(override val message: String, val errorCode: String): Exception()