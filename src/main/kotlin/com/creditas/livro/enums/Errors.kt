package com.creditas.livro.enums

enum class Errors(val code: String, val message:String) {
    L101("L-101", "Book [%s] not exists"),
    L102("L-102", "Cannot update book with status [%s]"),
    L201("L-201", "Customer [%s] not exists"),
    L301("L-301", "Unknown Error")
}