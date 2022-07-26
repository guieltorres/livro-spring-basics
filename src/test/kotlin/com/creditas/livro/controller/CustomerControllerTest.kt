package com.creditas.livro.controller

import com.creditas.livro.controller.request.PostCustomerRequest
import com.creditas.livro.controller.request.PutCustomerRequest
import com.creditas.livro.enums.CustomerStatus
import com.creditas.livro.helper.buildCustomer
import com.creditas.livro.repository.CustomerRepository
import com.creditas.livro.security.UserCustomDetails
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.random.Random


@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles("test")
@WithMockUser
class CustomerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should return all customers when get all`() {
        val customer1 = customerRepository.save(buildCustomer())
        val customer2 = customerRepository.save(buildCustomer())

        mockMvc.perform(get("/customers"))
            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$.content.[0].id").value(customer1.id))
            .andExpect(jsonPath("$.content.[0].name").value(customer1.name))
            .andExpect(jsonPath("$.content.[0].email").value(customer1.email))
            .andExpect(jsonPath("$.content.[0].status").value(customer1.status.name))
            .andExpect(jsonPath("$.content.[1].id").value(customer2.id))
            .andExpect(jsonPath("$.content.[1].name").value(customer2.name))
            .andExpect(jsonPath("$.content.[1].email").value(customer2.email))
            .andExpect(jsonPath("$.content.[1].status").value(customer2.status.name))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should filter all customers by name when get all`() {
        val customer = customerRepository.save(buildCustomer(name = "Guilherme"))
        customerRepository.save(buildCustomer(name = "Beatriz"))

        mockMvc.perform(get("/customers?name=Guilherme"))
            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$.content.[0].id").value(customer.id))
            .andExpect(jsonPath("$.content.[0].name").value(customer.name))
            .andExpect(jsonPath("$.content.[0].email").value(customer.email))
            .andExpect(jsonPath("$.content.[0].status").value(customer.status.name))
    }

    @Test
    fun `should create customer`(){
        val request = PostCustomerRequest(name = "fake name", email = "#${Random.nextInt()}@fakemail.com", password = "123")

        mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated)

        val customers = customerRepository.findAll().toList()
        assertEquals(1, customers.size)
        assertEquals(request.name, customers[0].name)
        assertEquals(request.email, customers[0].email)
    }

    @Test
    fun `should throw error when create customer has invalid information`(){
        val request = PostCustomerRequest(name = "", email = "#${Random.nextInt()}@fakemail.com", password = "123")

        mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity)

//            .andExpect(jsonPath("$").value(422))
//            .andExpect(jsonPath("$.message").value("Nome deve ser informado"))
//            .andExpect(jsonPath("$.internalCode").value("L-001"))

    }

    @Test
    fun `should get user by id when user has the same id`() {
        val customer = customerRepository.save(buildCustomer())

        mockMvc.perform(get("/customers/${customer.id}").with(user(UserCustomDetails(customer))))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(customer.id))
            .andExpect(jsonPath("$.name").value(customer.name))
            .andExpect(jsonPath("$.email").value(customer.email))
            .andExpect(jsonPath("$.status").value(customer.status.name))
    }

    @Test
    fun `should return forbidden when user has the different id`() {
        val customer = customerRepository.save(buildCustomer())

        mockMvc.perform(get("/customers/0").with(user(UserCustomDetails(customer))))
            .andExpect(status().isForbidden)
//            .andExpect(jsonPath("$").value(403))
//            .andExpect(jsonPath("$.message").value("Access Denied"))
//            .andExpect(jsonPath("$.internalCode").value("L-000"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should get user by id when user is admin`() {
        val customer = customerRepository.save(buildCustomer())

        mockMvc.perform(get("/customers/${customer.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(customer.id))
            .andExpect(jsonPath("$.name").value(customer.name))
            .andExpect(jsonPath("$.email").value(customer.email))
            .andExpect(jsonPath("$.status").value(customer.status.name))
    }

    @Test
    fun `should update customer`() {
        val customer = customerRepository.save(buildCustomer())
        val request = PutCustomerRequest("Guilherme", "emailUpdate@email.com")

        mockMvc.perform(put("/customers/${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)).with(user(UserCustomDetails(customer))))
            .andExpect(status().isNoContent)

        val customers = customerRepository.findAll().toList()
        assertEquals(1, customers.size)
        assertEquals(request.name, customers[0].name)
        assertEquals(request.email, customers[0].email)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should return not found when update customer do not exists`() {
        val request = PutCustomerRequest("Guilherme", "emailUpdate@email.com")

        mockMvc.perform(put("/customers/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound)

    }

    @Test
    fun `should throw error when update customer has invalid information`() {
        val request = PutCustomerRequest("", "emailUpdate@email.com")

        mockMvc.perform(put("/customers/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun `should delete customer`() {
        val customer = customerRepository.save(buildCustomer())

        mockMvc.perform(delete("/customers/${customer.id}").with(user(UserCustomDetails(customer))))
            .andExpect(status().isNoContent)

        val customerDeleted = customerRepository.findById(customer.id!!)
        assertEquals(CustomerStatus.INATIVO, customerDeleted.get().status)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should return not found when delete customer do not exists`() {
        mockMvc.perform(delete("/customers/1"))
            .andExpect(status().isNotFound)
    }

}