package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.Subscriber
import com.beautypoint.app.repository.SubscriberRepository
import com.beautypoint.app.repository.search.SubscriberSearchRepository
import com.beautypoint.app.service.SubscriberQueryService
import com.beautypoint.app.service.SubscriberService
import com.beautypoint.app.web.rest.TestUtil.createFormattingConversionService
import com.beautypoint.app.web.rest.errors.ExceptionTranslator
import org.assertj.core.api.Assertions.assertThat
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.*
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Test class for the SubscriberResource REST controller.
 *
 * @see SubscriberResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class SubscriberResourceIT {

    @Autowired
    private lateinit var subscriberRepository: SubscriberRepository

    @Autowired
    private lateinit var subscriberService: SubscriberService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.SubscriberSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockSubscriberSearchRepository: SubscriberSearchRepository

    @Autowired
    private lateinit var subscriberQueryService: SubscriberQueryService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restSubscriberMockMvc: MockMvc

    private lateinit var subscriber: Subscriber

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val subscriberResource = SubscriberResource(subscriberService, subscriberQueryService)
        this.restSubscriberMockMvc = MockMvcBuilders.standaloneSetup(subscriberResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        subscriber = createEntity(em)
    }

    @Test
    @Transactional
    fun createSubscriber() {
        val databaseSizeBeforeCreate = subscriberRepository.findAll().size

        // Create the Subscriber
        restSubscriberMockMvc.perform(
            post("/api/subscribers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriber))
        ).andExpect(status().isCreated)

        // Validate the Subscriber in the database
        val subscriberList = subscriberRepository.findAll()
        assertThat(subscriberList).hasSize(databaseSizeBeforeCreate + 1)
        val testSubscriber = subscriberList[subscriberList.size - 1]
        assertThat(testSubscriber.firsName).isEqualTo(DEFAULT_FIRS_NAME)
        assertThat(testSubscriber.email).isEqualTo(DEFAULT_EMAIL)

        // Validate the Subscriber in Elasticsearch
        verify(mockSubscriberSearchRepository, times(1)).save(testSubscriber)
    }

    @Test
    @Transactional
    fun createSubscriberWithExistingId() {
        val databaseSizeBeforeCreate = subscriberRepository.findAll().size

        // Create the Subscriber with an existing ID
        subscriber.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubscriberMockMvc.perform(
            post("/api/subscribers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriber))
        ).andExpect(status().isBadRequest)

        // Validate the Subscriber in the database
        val subscriberList = subscriberRepository.findAll()
        assertThat(subscriberList).hasSize(databaseSizeBeforeCreate)

        // Validate the Subscriber in Elasticsearch
        verify(mockSubscriberSearchRepository, times(0)).save(subscriber)
    }


    @Test
    @Transactional
    fun checkEmailIsRequired() {
        val databaseSizeBeforeTest = subscriberRepository.findAll().size
        // set the field null
        subscriber.email = null

        // Create the Subscriber, which fails.

        restSubscriberMockMvc.perform(
            post("/api/subscribers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriber))
        ).andExpect(status().isBadRequest)

        val subscriberList = subscriberRepository.findAll()
        assertThat(subscriberList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllSubscribers() {
        // Initialize the database
        subscriberRepository.saveAndFlush(subscriber)

        // Get all the subscriberList
        restSubscriberMockMvc.perform(get("/api/subscribers?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscriber.id?.toInt())))
            .andExpect(jsonPath("$.[*].firsName").value(hasItem(DEFAULT_FIRS_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
    }

    @Test
    @Transactional
    fun getSubscriber() {
        // Initialize the database
        subscriberRepository.saveAndFlush(subscriber)

        val id = subscriber.id
        assertNotNull(id)

        // Get the subscriber
        restSubscriberMockMvc.perform(get("/api/subscribers/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.firsName").value(DEFAULT_FIRS_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
    }

    @Test
    @Transactional
    fun getAllSubscribersByFirsNameIsEqualToSomething() {
        // Initialize the database
        subscriberRepository.saveAndFlush(subscriber)

        // Get all the subscriberList where firsName equals to DEFAULT_FIRS_NAME
        defaultSubscriberShouldBeFound("firsName.equals=$DEFAULT_FIRS_NAME")

        // Get all the subscriberList where firsName equals to UPDATED_FIRS_NAME
        defaultSubscriberShouldNotBeFound("firsName.equals=$UPDATED_FIRS_NAME")
    }

    @Test
    @Transactional
    fun getAllSubscribersByFirsNameIsInShouldWork() {
        // Initialize the database
        subscriberRepository.saveAndFlush(subscriber)

        // Get all the subscriberList where firsName in DEFAULT_FIRS_NAME or UPDATED_FIRS_NAME
        defaultSubscriberShouldBeFound("firsName.in=$DEFAULT_FIRS_NAME,$UPDATED_FIRS_NAME")

        // Get all the subscriberList where firsName equals to UPDATED_FIRS_NAME
        defaultSubscriberShouldNotBeFound("firsName.in=$UPDATED_FIRS_NAME")
    }

    @Test
    @Transactional
    fun getAllSubscribersByFirsNameIsNullOrNotNull() {
        // Initialize the database
        subscriberRepository.saveAndFlush(subscriber)

        // Get all the subscriberList where firsName is not null
        defaultSubscriberShouldBeFound("firsName.specified=true")

        // Get all the subscriberList where firsName is null
        defaultSubscriberShouldNotBeFound("firsName.specified=false")
    }

    @Test
    @Transactional
    fun getAllSubscribersByEmailIsEqualToSomething() {
        // Initialize the database
        subscriberRepository.saveAndFlush(subscriber)

        // Get all the subscriberList where email equals to DEFAULT_EMAIL
        defaultSubscriberShouldBeFound("email.equals=$DEFAULT_EMAIL")

        // Get all the subscriberList where email equals to UPDATED_EMAIL
        defaultSubscriberShouldNotBeFound("email.equals=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    fun getAllSubscribersByEmailIsInShouldWork() {
        // Initialize the database
        subscriberRepository.saveAndFlush(subscriber)

        // Get all the subscriberList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultSubscriberShouldBeFound("email.in=$DEFAULT_EMAIL,$UPDATED_EMAIL")

        // Get all the subscriberList where email equals to UPDATED_EMAIL
        defaultSubscriberShouldNotBeFound("email.in=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    fun getAllSubscribersByEmailIsNullOrNotNull() {
        // Initialize the database
        subscriberRepository.saveAndFlush(subscriber)

        // Get all the subscriberList where email is not null
        defaultSubscriberShouldBeFound("email.specified=true")

        // Get all the subscriberList where email is null
        defaultSubscriberShouldNotBeFound("email.specified=false")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultSubscriberShouldBeFound(filter: String) {
        restSubscriberMockMvc.perform(get("/api/subscribers?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscriber.id?.toInt())))
            .andExpect(jsonPath("$.[*].firsName").value(hasItem(DEFAULT_FIRS_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))

        // Check, that the count call also returns 1
        restSubscriberMockMvc.perform(get("/api/subscribers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultSubscriberShouldNotBeFound(filter: String) {
        restSubscriberMockMvc.perform(get("/api/subscribers?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restSubscriberMockMvc.perform(get("/api/subscribers/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingSubscriber() {
        // Get the subscriber
        restSubscriberMockMvc.perform(get("/api/subscribers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateSubscriber() {
        // Initialize the database
        subscriberService.save(subscriber)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSubscriberSearchRepository)

        val databaseSizeBeforeUpdate = subscriberRepository.findAll().size

        // Update the subscriber
        val id = subscriber.id
        assertNotNull(id)
        val updatedSubscriber = subscriberRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedSubscriber are not directly saved in db
        em.detach(updatedSubscriber)
        updatedSubscriber.firsName = UPDATED_FIRS_NAME
        updatedSubscriber.email = UPDATED_EMAIL

        restSubscriberMockMvc.perform(
            put("/api/subscribers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSubscriber))
        ).andExpect(status().isOk)

        // Validate the Subscriber in the database
        val subscriberList = subscriberRepository.findAll()
        assertThat(subscriberList).hasSize(databaseSizeBeforeUpdate)
        val testSubscriber = subscriberList[subscriberList.size - 1]
        assertThat(testSubscriber.firsName).isEqualTo(UPDATED_FIRS_NAME)
        assertThat(testSubscriber.email).isEqualTo(UPDATED_EMAIL)

        // Validate the Subscriber in Elasticsearch
        verify(mockSubscriberSearchRepository, times(1)).save(testSubscriber)
    }

    @Test
    @Transactional
    fun updateNonExistingSubscriber() {
        val databaseSizeBeforeUpdate = subscriberRepository.findAll().size

        // Create the Subscriber

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscriberMockMvc.perform(
            put("/api/subscribers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subscriber))
        ).andExpect(status().isBadRequest)

        // Validate the Subscriber in the database
        val subscriberList = subscriberRepository.findAll()
        assertThat(subscriberList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Subscriber in Elasticsearch
        verify(mockSubscriberSearchRepository, times(0)).save(subscriber)
    }

    @Test
    @Transactional
    fun deleteSubscriber() {
        // Initialize the database
        subscriberService.save(subscriber)

        val databaseSizeBeforeDelete = subscriberRepository.findAll().size

        val id = subscriber.id
        assertNotNull(id)

        // Delete the subscriber
        restSubscriberMockMvc.perform(
            delete("/api/subscribers/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val subscriberList = subscriberRepository.findAll()
        assertThat(subscriberList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Subscriber in Elasticsearch
        verify(mockSubscriberSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchSubscriber() {
        // Initialize the database
        subscriberService.save(subscriber)
        `when`(mockSubscriberSearchRepository.search(queryStringQuery("id:" + subscriber.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(subscriber), PageRequest.of(0, 1), 1))
        // Search the subscriber
        restSubscriberMockMvc.perform(get("/api/_search/subscribers?query=id:" + subscriber.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscriber.id?.toInt())))
            .andExpect(jsonPath("$.[*].firsName").value(hasItem(DEFAULT_FIRS_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(Subscriber::class.java)
        val subscriber1 = Subscriber()
        subscriber1.id = 1L
        val subscriber2 = Subscriber()
        subscriber2.id = subscriber1.id
        assertThat(subscriber1).isEqualTo(subscriber2)
        subscriber2.id = 2L
        assertThat(subscriber1).isNotEqualTo(subscriber2)
        subscriber1.id = null
        assertThat(subscriber1).isNotEqualTo(subscriber2)
    }

    companion object {

        private const val DEFAULT_FIRS_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_FIRS_NAME = "BBBBBBBBBB"

        private const val DEFAULT_EMAIL: String = "AAAAAAAAAA"
        private const val UPDATED_EMAIL = "BBBBBBBBBB"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Subscriber {
            val subscriber = Subscriber()
            subscriber.firsName = DEFAULT_FIRS_NAME
            subscriber.email = DEFAULT_EMAIL

            return subscriber
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Subscriber {
            val subscriber = Subscriber()
            subscriber.firsName = UPDATED_FIRS_NAME
            subscriber.email = UPDATED_EMAIL

            return subscriber
        }
    }
}
