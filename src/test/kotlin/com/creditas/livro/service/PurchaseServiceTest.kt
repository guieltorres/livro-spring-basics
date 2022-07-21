package com.creditas.livro.service

import com.creditas.livro.events.PurchaseEvent
import com.creditas.livro.helper.buildCustomer
import com.creditas.livro.helper.buildPurchase
import com.creditas.livro.model.BookModel
import com.creditas.livro.model.CustomerModel
import com.creditas.livro.model.PurchaseModel
import com.creditas.livro.repository.PurchaseRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockKExtension::class)
class PurchaseServiceTest {

    @MockK
    private lateinit var purchaseRepository: PurchaseRepository

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var purchaseService: PurchaseService

    private val purchaseEventSlot = slot<PurchaseEvent>()

    @Test
    fun `should create purchase and publish event`(){
        val purchase = buildPurchase()

        every { purchaseRepository.save(purchase) } returns purchase

        every { applicationEventPublisher.publishEvent(any()) } just runs

        purchaseService.create(purchase)

        verify (exactly = 1) { purchaseRepository.save(purchase) }
        verify (exactly = 1) { applicationEventPublisher.publishEvent(capture(purchaseEventSlot)) }

        assertEquals(purchase, purchaseEventSlot.captured.purchaseModel)
    }

    @Test
    fun `should upda purchase`() {
        val purchase = buildPurchase()

        every { purchaseRepository.save(purchase) } returns purchase

        purchaseService.update(purchase)

        verify (exactly = 1) { purchaseRepository.save(purchase) }
    }

}