package com.creditas.livro.controller.response

import com.creditas.livro.enums.BookStatus
import com.creditas.livro.model.CustomerModel
import java.math.BigDecimal

data class BookResponse (
    var id: Int? = null,

    var name : String,

    var price : BigDecimal,

    var customer: CustomerModel? = null,

    var status: BookStatus? = null
)
