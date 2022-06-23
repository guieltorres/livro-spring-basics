package com.creditas.livro.events.listener

import com.creditas.livro.events.PurchaseEvent
import com.creditas.livro.service.BookService
import com.creditas.livro.service.PurchaseService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UpdateSoldBookListener(
    private val bookService: BookService
) {
    @Async
    @EventListener
    fun listen(purchaseEvent: PurchaseEvent){
        bookService.purchase(purchaseEvent.purchaseModel.books)
    }
}