package com.creditas.livro.exception

class AuthenticationException(override val message: String, val errorCode: String): Exception()