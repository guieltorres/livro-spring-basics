package com.creditas.livro.model

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
    var email : String
    )