package com.creditas.livro.model

import com.creditas.livro.enums.BookStatus
import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "customer")
data class BookModel (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column
    var name : String,

    @Column
    var price : BigDecimal,

    @Column
    @Enumerated(EnumType.STRING)
    var status: BookStatus? = null,

    @ManyToOne
    @JoinColumn(name = "customer_id")
    var customer: CustomerModel? = null
)