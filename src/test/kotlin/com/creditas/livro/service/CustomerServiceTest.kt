package com.creditas.livro.service

import com.creditas.livro.enums.CustomerStatus
import com.creditas.livro.enums.Errors
import com.creditas.livro.enums.Role
import com.creditas.livro.exception.NotFoundException
import com.creditas.livro.helper.buildCustomer
import com.creditas.livro.model.CustomerModel
import com.creditas.livro.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    private lateinit var customerRepository: CustomerRepository

    @MockK
    private lateinit var bookService: BookService

    @MockK
    private lateinit var bCrypt: BCryptPasswordEncoder

    @InjectMockKs
    @SpyK
    private lateinit var customerService: CustomerService

    @Test
    fun `should return all customers`(){
        val fakeCustomers = listOf(buildCustomer(), buildCustomer(), buildCustomer())
        val pageableFakeCustomer = PageImpl(fakeCustomers)

        every { customerRepository.findAll(PageRequest.of(0, 10)) } returns pageableFakeCustomer

        val customers = customerService.getAll(null, PageRequest.of(0, 10))

        assertEquals(pageableFakeCustomer, customers)
        verify(exactly = 1) {customerRepository.findAll(PageRequest.of(0, 10))}
        verify(exactly = 0) { customerRepository.findByNameContaining(any(), PageRequest.of(0, 10)) }
    }

    @Test
    fun `should return customers when name is informed`(){
        val name = UUID.randomUUID().toString()
        val fakeCustomers = listOf(buildCustomer(), buildCustomer(), buildCustomer())
        val pageableFakeCustomer = PageImpl(fakeCustomers)

        every { customerRepository.findByNameContaining(name, PageRequest.of(0, 10)) } returns pageableFakeCustomer

        val customers = customerService.getAll(name, PageRequest.of(0, 10))

        assertEquals(pageableFakeCustomer, customers)
        verify(exactly = 0) {customerRepository.findAll(PageRequest.of(0, 10))}
        verify(exactly = 1) { customerRepository.findByNameContaining(name, PageRequest.of(0, 10)) }
    }

    @Test
    fun `should create customer and encrypt password`() {
        val initialPassword = Random().nextInt().toString()
        val fakeCustomer = buildCustomer(password = initialPassword)
        val fakePassword = UUID.randomUUID().toString()
        val fakeCustomerEcrypted = fakeCustomer.copy(password = fakePassword)

        every { customerRepository.save(fakeCustomerEcrypted)} returns fakeCustomer
        every {bCrypt.encode(initialPassword)} returns fakePassword

        customerService.create(fakeCustomer)

        verify(exactly = 1) {customerRepository.save(fakeCustomerEcrypted)}
        verify(exactly = 1) {bCrypt.encode(initialPassword)}
    }

    @Test
    fun `should return customer by id`(){
        val id = Random().nextInt()
        val fakeCustomer = buildCustomer(id = id)

        every { customerRepository.findById(id) } returns Optional.of(fakeCustomer)

        val customer = customerService.findById(id)

        assertEquals(fakeCustomer, customer)
        verify(exactly = 1) { customerRepository.findById(id) }
    }

    @Test
    fun `should throw error when not found by id`(){
        val id = Random().nextInt()

        every { customerRepository.findById(id) } returns Optional.empty()

        val error = assertThrows<NotFoundException>{ (customerService.findById(id)) }

        assertEquals("Customer [${id}] not exists", error.message)
        assertEquals("L-201", error.errorCode)
        verify(exactly = 1) { customerRepository.findById(id) }
    }

    @Test
    fun `should update customer`(){
        val id = Random().nextInt()
        val fakeCustomer = buildCustomer(id = id)

        every { customerRepository.existsById(id) } returns true
        every { customerRepository.save(fakeCustomer) } returns fakeCustomer

        customerService.update(fakeCustomer)

        verify(exactly = 1) { customerRepository.existsById(id) }
        verify(exactly = 1) { customerRepository.save(fakeCustomer) }
    }

    @Test
    fun `should throw error when not found when update customer`(){
        val id = Random().nextInt()
        val fakeCustomer = buildCustomer(id = id)

        every { customerRepository.existsById(id) } returns false
        every { customerRepository.save(fakeCustomer) } returns fakeCustomer

        val error = assertThrows<NotFoundException>{ (customerService.update(fakeCustomer)) }

        assertEquals("Customer [${id}] not exists", error.message)
        assertEquals("L-201", error.errorCode)

        verify(exactly = 1) { customerRepository.existsById(id) }
        verify(exactly = 0) { customerRepository.save(any()) }
    }

    @Test
    fun `should delete customer`() {
        val id = Random().nextInt()
        val fakeCustomer = buildCustomer(id = id)
        val expectedCustomer = fakeCustomer.copy(status = CustomerStatus.INATIVO)

        every { customerService.findById(id) } returns fakeCustomer
        every { customerRepository.save(expectedCustomer) } returns expectedCustomer
        every { bookService.deleteByCustomer(fakeCustomer) } just runs

        customerService.delete(id)

        verify(exactly = 1) { customerService.findById(id) }
        verify(exactly = 1) { bookService.deleteByCustomer(fakeCustomer) }
        verify(exactly = 1) { customerRepository.save(expectedCustomer) }
    }

    @Test
    fun `should throw not found exception when delete customer`() {
        val id = Random().nextInt()

        every { customerService.findById(id) } throws NotFoundException(Errors.L201.message.format(id), Errors.L201.code)

        val error = assertThrows<NotFoundException>{ customerService.delete(id) }

        assertEquals("Customer [${id}] not exists", error.message)
        assertEquals("L-201", error.errorCode)

        verify(exactly = 1) { customerService.findById(id) }
        verify(exactly = 0) { bookService.deleteByCustomer(any()) }
        verify(exactly = 0) { customerRepository.save(any()) }
    }

    @Test
    fun `should return true when email available`() {
        val email = "${UUID.randomUUID().toString()}@email.com"

        every { customerRepository.existsByEmail(email) } returns false

        val emailAvailable = customerService.emailAvailable(email)

        assertTrue(emailAvailable)
        verify(exactly = 1) { customerRepository.existsByEmail(email) }
    }

    @Test
    fun `should return false when email unavailable`() {
        val email = "${UUID.randomUUID()}@email.com"

        every { customerRepository.existsByEmail(email) } returns true

        val emailAvailable = customerService.emailAvailable(email)

        assertFalse(emailAvailable)
        verify(exactly = 1) { customerRepository.existsByEmail(email) }
    }

}