package com.creditas.livro.service

import com.creditas.livro.enums.BookStatus
import com.creditas.livro.enums.Errors
import com.creditas.livro.exception.NotFoundException
import com.creditas.livro.model.BookModel
import com.creditas.livro.model.CustomerModel
import com.creditas.livro.repository.BookRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    val bookRepository: BookRepository
) {
    fun create(book: BookModel) {
        bookRepository.save(book)
    }

    fun findAll(pageable: Pageable): Page<BookModel> {
        return bookRepository.findAll(pageable)
    }

    fun findActives(pageable: Pageable): Page<BookModel> =
        bookRepository.findByStatus(BookStatus.ATIVO, pageable)

    fun findById(id: Int): BookModel {
        return bookRepository.findById(id).orElseThrow { NotFoundException(Errors.L101.message.format(id), Errors.L101.code) }
    }

    fun delete(id: Int) {
        val book = findById(id)

        book.status = BookStatus.CANCELADO

        update(book)

    }

    fun update(book: BookModel) {
        bookRepository.save(book)
    }

    @Transactional
    fun deleteByCustomer(customer: CustomerModel) {
        val books = bookRepository.findByCustomer(customer)
        for(book in books){
            book.status = BookStatus.DELETADO
        }
        bookRepository.saveAll(books)
    }

    fun findAllByIds(bookIds: Set<Int>): List<BookModel> {
        return bookRepository.findAllById(bookIds).toList()
    }

    fun purchase(books: MutableList<BookModel>) {
        books.map {
            it.status = BookStatus.VENDIDO
        }
        bookRepository.saveAll(books)
    }
}
