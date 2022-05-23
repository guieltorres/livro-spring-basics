package com.creditas.livro.repository

import com.creditas.livro.model.CustomerModel
import org.springframework.data.repository.CrudRepository

interface CustomerRepository : CrudRepository<CustomerModel, Int>{

    fun findByNameContaining(name: String): List<CustomerModel>

}