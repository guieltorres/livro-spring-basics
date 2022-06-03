package com.creditas.livro.model

import com.creditas.livro.enums.CustomerStatus
import javax.persistence.*

@Entity
@Table(name = "customer")
data class CustomerModel (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column
    var name : String,

    @Column
    var email : String,

    @Column
    @Enumerated(EnumType.STRING)
    var status: CustomerStatus
)