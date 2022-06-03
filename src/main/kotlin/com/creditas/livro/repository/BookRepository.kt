package com.creditas.livro.repository

import com.creditas.livro.enums.BookStatus
import com.creditas.livro.model.BookModel
import com.creditas.livro.model.CustomerModel
import org.springframework.data.repository.CrudRepository

interface BookRepository : CrudRepository<BookModel, Int>{

    fun findByStatus(status: BookStatus): List<BookModel>
    fun findByCustomer(customer: CustomerModel): List<BookModel>

}