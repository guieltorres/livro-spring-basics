package com.creditas.livro.exception

class NotFoundException(override val message: String, val errorCode: String): Exception()