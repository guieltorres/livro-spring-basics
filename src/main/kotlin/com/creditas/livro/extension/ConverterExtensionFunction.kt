package com.creditas.livro.extension

import com.creditas.livro.controller.request.PostBookRequest
import com.creditas.livro.controller.request.PostCustomerRequest
import com.creditas.livro.controller.request.PutBookRequest
import com.creditas.livro.controller.request.PutCustomerRequest
import com.creditas.livro.controller.response.BookResponse
import com.creditas.livro.controller.response.CustomerResponse
import com.creditas.livro.controller.response.PageResponse
import com.creditas.livro.enums.BookStatus
import com.creditas.livro.enums.CustomerStatus
import com.creditas.livro.model.BookModel
import com.creditas.livro.model.CustomerModel
import org.springframework.data.domain.Page

fun PostCustomerRequest.toCustomerModel(): CustomerModel {
    return CustomerModel(name = this.name,
        email=this.email,
        status = CustomerStatus.ATIVO,
        password = this.password)
}

fun PutCustomerRequest.toCustomerModel(previousCustomer: CustomerModel): CustomerModel {
    return CustomerModel(id = previousCustomer.id,
        name = this.name,
        email = this.email,
        status = previousCustomer.status,
        password = previousCustomer.password)
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

fun CustomerModel.toResponse(): CustomerResponse {
    return CustomerResponse(
        id = this.id,
        name = this.name,
        email = this.email,
        status = this.status
    )
}

fun BookModel.toResponse(): BookResponse {
    return BookResponse(
        id = this.id,
        name = this.name,
        price = this.price,
        customer = this.customer,
        status = this.status
    )
}

fun <T> Page<T>.toPageResponse(): PageResponse<T> {
    return PageResponse(this.content,
        this.number,
        this.totalElements,
        this.totalPages)
}