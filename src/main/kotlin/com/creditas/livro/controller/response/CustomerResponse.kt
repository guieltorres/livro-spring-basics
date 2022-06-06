package com.creditas.livro.controller.response

import com.creditas.livro.enums.CustomerStatus

data class CustomerResponse (
    var id: Int? = null,

    var name : String,

    var email : String,

    var status: CustomerStatus
)