package com.creditas.livro.controller.mapper

import com.creditas.livro.controller.request.PostPurchaseRequest
import com.creditas.livro.model.PurchaseModel
import com.creditas.livro.service.BookService
import com.creditas.livro.service.CustomerService
import org.springframework.stereotype.Component

@Component
class PurchaseMapper(
    private val bookService: BookService,
    private val customerService: CustomerService
) {
    fun toModel(request: PostPurchaseRequest): PurchaseModel {
        val customer = customerService.findById(request.customerId)
        val books = bookService.findAllByIds(request.bookIds)

        return PurchaseModel(
            customer = customer,
            books = books.toMutableList(),
            price = books.sumOf { it.price }
        )
    }
}