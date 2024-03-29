package com.creditas.livro.service

import com.creditas.livro.enums.CustomerStatus
import com.creditas.livro.enums.Errors
import com.creditas.livro.enums.Role
import com.creditas.livro.exception.NotFoundException
import com.creditas.livro.model.CustomerModel
import com.creditas.livro.repository.CustomerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val bookService: BookService,
    private val bCrypt: BCryptPasswordEncoder
    ) {

    fun getAll(name: String?, pageable: Pageable): Page<CustomerModel> {
        name?.let{
            return customerRepository.findByNameContaining(it, pageable)
        }
        return customerRepository.findAll(pageable)
    }

    fun create(customer: CustomerModel) {
        val customerCopy = customer.copy(
            roles = setOf(Role.CUSTOMER),
            password = bCrypt.encode(customer.password)
        )
        customerRepository.save(customerCopy)
    }

    fun findById(id: Int): CustomerModel {
        return customerRepository.findById(id).orElseThrow { NotFoundException(Errors.L201.message.format(id), Errors.L201.code)  }
    }

    fun update(customer: CustomerModel) {
        if(!customerRepository.existsById(customer.id!!)){
            throw NotFoundException(Errors.L201.message.format(customer.id), Errors.L201.code)
        }
        customerRepository.save(customer)
    }

    fun delete(id: Int) {
        val customer = findById(id)
        bookService.deleteByCustomer(customer)

        customer.status = CustomerStatus.INATIVO

        customerRepository.save(customer)
    }

    fun emailAvailable(email: String): Boolean {
        return !customerRepository.existsByEmail(email)
    }
}