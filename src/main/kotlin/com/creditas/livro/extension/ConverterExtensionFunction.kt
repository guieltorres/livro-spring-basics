package com.creditas.livro.extension

import com.creditas.livro.controller.request.PostBookRequest
import com.creditas.livro.controller.request.PostCustomerRequest
import com.creditas.livro.controller.request.PutBookRequest
import com.creditas.livro.controller.request.PutCustomerRequest
import com.creditas.livro.enums.BookStatus
import com.creditas.livro.model.BookModel
import com.creditas.livro.model.CustomerModel

fun PostCustomerRequest.toCustomerModel(): CustomerModel {
    return CustomerModel(name = this.name, email=this.email)
}

fun PutCustomerRequest.toCustomerModel(id: Int): CustomerModel {
    return CustomerModel(id = id, name = this.name, email = this.email)
}

fun PostBookRequest.toBookModel(customer: CustomerModel): BookModel {
    return BookModel(
        name = this.name,
        price= this.price,
        status = BookStatus.ATIVO,
        customer = customer
    )
}

fun PutBookRequest.toBookModel(previousBook: BookModel): BookModel {
    return BookModel(
        id = previousBook.id,
        name = this.name ?: previousBook.name,
        price = this.price ?: previousBook.price,
        status = previousBook.status,
        customer = previousBook.customer
    )
}