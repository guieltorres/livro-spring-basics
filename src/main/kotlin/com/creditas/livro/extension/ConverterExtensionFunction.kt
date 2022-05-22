package com.creditas.livro.extension

import com.creditas.livro.controller.request.PostCustomerRequest
import com.creditas.livro.controller.request.PutCustomerRequest
import com.creditas.livro.model.CustomerModel

fun PostCustomerRequest.toCustomerModel(): CustomerModel {
    return CustomerModel(name = this.name, email=this.email)
}

fun PutCustomerRequest.toCustomerModel(id: Int): CustomerModel {
    return CustomerModel(id = id, name = this.name, email = this.email)
}