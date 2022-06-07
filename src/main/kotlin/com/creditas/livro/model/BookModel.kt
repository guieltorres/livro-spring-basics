package com.creditas.livro.model

import com.creditas.livro.enums.BookStatus
import com.creditas.livro.enums.Errors
import com.creditas.livro.exception.BadRequestException
import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "book")
data class BookModel (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column
    var name : String,

    @Column
    var price : BigDecimal,

    @ManyToOne
    @JoinColumn(name = "customer_id")
    var customer: CustomerModel? = null
){
    @Column
    @Enumerated(EnumType.STRING)
    var status: BookStatus? = null
        set(value){
            if (field == BookStatus.CANCELADO || field == BookStatus.DELETADO){
                throw BadRequestException(Errors.L102.message.format(field), Errors.L102.code)
            }
            field = value
        }
    constructor(id: Int? = null,
                name: String,
                price: BigDecimal,
                customer: CustomerModel? = null,
                status: BookStatus?): this(id, name, price, customer) {
                    this.status = status
                }
}