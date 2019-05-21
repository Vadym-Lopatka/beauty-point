package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.Option
import com.beautypoint.app.repository.OptionRepository
import com.beautypoint.app.repository.search.OptionSearchRepository
import com.beautypoint.app.service.OptionQueryService
import com.beautypoint.app.service.OptionService
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
import java.math.BigDecimal
import java.util.*
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Test class for the OptionResource REST controller.
 *
 * @see OptionResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class OptionResourceIT {

    @Autowired
    private lateinit var optionRepository: OptionRepository

    @Autowired
    private lateinit var optionService: OptionService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.OptionSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockOptionSearchRepository: OptionSearchRepository

    @Autowired
    private lateinit var optionQueryService: OptionQueryService

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

    private lateinit var restOptionMockMvc: MockMvc

    private lateinit var option: Option

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val optionResource = OptionResource(optionService, optionQueryService)
        this.restOptionMockMvc = MockMvcBuilders.standaloneSetup(optionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        option = createEntity(em)
    }

    @Test
    @Transactional
    fun createOption() {
        val databaseSizeBeforeCreate = optionRepository.findAll().size

        // Create the Option
        restOptionMockMvc.perform(
            post("/api/options")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(option))
        ).andExpect(status().isCreated)

        // Validate the Option in the database
        val optionList = optionRepository.findAll()
        assertThat(optionList).hasSize(databaseSizeBeforeCreate + 1)
        val testOption = optionList[optionList.size - 1]
        assertThat(testOption.name).isEqualTo(DEFAULT_NAME)
        assertThat(testOption.price).isEqualTo(DEFAULT_PRICE)
        assertThat(testOption.sessionTime).isEqualTo(DEFAULT_SESSION_TIME)
        assertThat(testOption.active).isEqualTo(DEFAULT_ACTIVE)

        // Validate the Option in Elasticsearch
        verify(mockOptionSearchRepository, times(1)).save(testOption)
    }

    @Test
    @Transactional
    fun createOptionWithExistingId() {
        val databaseSizeBeforeCreate = optionRepository.findAll().size

        // Create the Option with an existing ID
        option.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restOptionMockMvc.perform(
            post("/api/options")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(option))
        ).andExpect(status().isBadRequest)

        // Validate the Option in the database
        val optionList = optionRepository.findAll()
        assertThat(optionList).hasSize(databaseSizeBeforeCreate)

        // Validate the Option in Elasticsearch
        verify(mockOptionSearchRepository, times(0)).save(option)
    }


    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = optionRepository.findAll().size
        // set the field null
        option.name = null

        // Create the Option, which fails.

        restOptionMockMvc.perform(
            post("/api/options")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(option))
        ).andExpect(status().isBadRequest)

        val optionList = optionRepository.findAll()
        assertThat(optionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPriceIsRequired() {
        val databaseSizeBeforeTest = optionRepository.findAll().size
        // set the field null
        option.price = null

        // Create the Option, which fails.

        restOptionMockMvc.perform(
            post("/api/options")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(option))
        ).andExpect(status().isBadRequest)

        val optionList = optionRepository.findAll()
        assertThat(optionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkSessionTimeIsRequired() {
        val databaseSizeBeforeTest = optionRepository.findAll().size
        // set the field null
        option.sessionTime = null

        // Create the Option, which fails.

        restOptionMockMvc.perform(
            post("/api/options")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(option))
        ).andExpect(status().isBadRequest)

        val optionList = optionRepository.findAll()
        assertThat(optionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkActiveIsRequired() {
        val databaseSizeBeforeTest = optionRepository.findAll().size
        // set the field null
        option.active = null

        // Create the Option, which fails.

        restOptionMockMvc.perform(
            post("/api/options")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(option))
        ).andExpect(status().isBadRequest)

        val optionList = optionRepository.findAll()
        assertThat(optionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllOptions() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList
        restOptionMockMvc.perform(get("/api/options?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(option.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toInt())))
            .andExpect(jsonPath("$.[*].sessionTime").value(hasItem(DEFAULT_SESSION_TIME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
    }

    @Test
    @Transactional
    fun getOption() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        val id = option.id
        assertNotNull(id)

        // Get the option
        restOptionMockMvc.perform(get("/api/options/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.toInt()))
            .andExpect(jsonPath("$.sessionTime").value(DEFAULT_SESSION_TIME))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
    }

    @Test
    @Transactional
    fun getAllOptionsByNameIsEqualToSomething() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where name equals to DEFAULT_NAME
        defaultOptionShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the optionList where name equals to UPDATED_NAME
        defaultOptionShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllOptionsByNameIsInShouldWork() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where name in DEFAULT_NAME or UPDATED_NAME
        defaultOptionShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the optionList where name equals to UPDATED_NAME
        defaultOptionShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllOptionsByNameIsNullOrNotNull() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where name is not null
        defaultOptionShouldBeFound("name.specified=true")

        // Get all the optionList where name is null
        defaultOptionShouldNotBeFound("name.specified=false")
    }

    @Test
    @Transactional
    fun getAllOptionsByPriceIsEqualToSomething() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where price equals to DEFAULT_PRICE
        defaultOptionShouldBeFound("price.equals=$DEFAULT_PRICE")

        // Get all the optionList where price equals to UPDATED_PRICE
        defaultOptionShouldNotBeFound("price.equals=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    fun getAllOptionsByPriceIsInShouldWork() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultOptionShouldBeFound("price.in=$DEFAULT_PRICE,$UPDATED_PRICE")

        // Get all the optionList where price equals to UPDATED_PRICE
        defaultOptionShouldNotBeFound("price.in=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    fun getAllOptionsByPriceIsNullOrNotNull() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where price is not null
        defaultOptionShouldBeFound("price.specified=true")

        // Get all the optionList where price is null
        defaultOptionShouldNotBeFound("price.specified=false")
    }

    @Test
    @Transactional
    fun getAllOptionsBySessionTimeIsEqualToSomething() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where sessionTime equals to DEFAULT_SESSION_TIME
        defaultOptionShouldBeFound("sessionTime.equals=$DEFAULT_SESSION_TIME")

        // Get all the optionList where sessionTime equals to UPDATED_SESSION_TIME
        defaultOptionShouldNotBeFound("sessionTime.equals=$UPDATED_SESSION_TIME")
    }

    @Test
    @Transactional
    fun getAllOptionsBySessionTimeIsInShouldWork() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where sessionTime in DEFAULT_SESSION_TIME or UPDATED_SESSION_TIME
        defaultOptionShouldBeFound("sessionTime.in=$DEFAULT_SESSION_TIME,$UPDATED_SESSION_TIME")

        // Get all the optionList where sessionTime equals to UPDATED_SESSION_TIME
        defaultOptionShouldNotBeFound("sessionTime.in=$UPDATED_SESSION_TIME")
    }

    @Test
    @Transactional
    fun getAllOptionsBySessionTimeIsNullOrNotNull() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where sessionTime is not null
        defaultOptionShouldBeFound("sessionTime.specified=true")

        // Get all the optionList where sessionTime is null
        defaultOptionShouldNotBeFound("sessionTime.specified=false")
    }

    @Test
    @Transactional
    fun getAllOptionsBySessionTimeIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where sessionTime greater than or equals to DEFAULT_SESSION_TIME
        defaultOptionShouldBeFound("sessionTime.greaterOrEqualThan=$DEFAULT_SESSION_TIME")

        // Get all the optionList where sessionTime greater than or equals to UPDATED_SESSION_TIME
        defaultOptionShouldNotBeFound("sessionTime.greaterOrEqualThan=$UPDATED_SESSION_TIME")
    }

    @Test
    @Transactional
    fun getAllOptionsBySessionTimeIsLessThanSomething() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where sessionTime less than or equals to DEFAULT_SESSION_TIME
        defaultOptionShouldNotBeFound("sessionTime.lessThan=$DEFAULT_SESSION_TIME")

        // Get all the optionList where sessionTime less than or equals to UPDATED_SESSION_TIME
        defaultOptionShouldBeFound("sessionTime.lessThan=$UPDATED_SESSION_TIME")
    }

    @Test
    @Transactional
    fun getAllOptionsByActiveIsEqualToSomething() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where active equals to DEFAULT_ACTIVE
        defaultOptionShouldBeFound("active.equals=$DEFAULT_ACTIVE")

        // Get all the optionList where active equals to UPDATED_ACTIVE
        defaultOptionShouldNotBeFound("active.equals=$UPDATED_ACTIVE")
    }

    @Test
    @Transactional
    fun getAllOptionsByActiveIsInShouldWork() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultOptionShouldBeFound("active.in=$DEFAULT_ACTIVE,$UPDATED_ACTIVE")

        // Get all the optionList where active equals to UPDATED_ACTIVE
        defaultOptionShouldNotBeFound("active.in=$UPDATED_ACTIVE")
    }

    @Test
    @Transactional
    fun getAllOptionsByActiveIsNullOrNotNull() {
        // Initialize the database
        optionRepository.saveAndFlush(option)

        // Get all the optionList where active is not null
        defaultOptionShouldBeFound("active.specified=true")

        // Get all the optionList where active is null
        defaultOptionShouldNotBeFound("active.specified=false")
    }

    @Test
    @Transactional
    fun getAllOptionsByOfferIsEqualToSomething() {
        // Initialize the database
        val offer = OfferResourceIT.createEntity(em)
        em.persist(offer)
        em.flush()
        option.offer = offer
        optionRepository.saveAndFlush(option)
        val offerId = offer.id

        // Get all the optionList where offer equals to offerId
        defaultOptionShouldBeFound("offerId.equals=$offerId")

        // Get all the optionList where offer equals to offerId + 1
        defaultOptionShouldNotBeFound("offerId.equals=${offerId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultOptionShouldBeFound(filter: String) {
        restOptionMockMvc.perform(get("/api/options?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(option.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toInt())))
            .andExpect(jsonPath("$.[*].sessionTime").value(hasItem(DEFAULT_SESSION_TIME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))

        // Check, that the count call also returns 1
        restOptionMockMvc.perform(get("/api/options/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultOptionShouldNotBeFound(filter: String) {
        restOptionMockMvc.perform(get("/api/options?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restOptionMockMvc.perform(get("/api/options/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingOption() {
        // Get the option
        restOptionMockMvc.perform(get("/api/options/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateOption() {
        // Initialize the database
        optionService.save(option)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockOptionSearchRepository)

        val databaseSizeBeforeUpdate = optionRepository.findAll().size

        // Update the option
        val id = option.id
        assertNotNull(id)
        val updatedOption = optionRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedOption are not directly saved in db
        em.detach(updatedOption)
        updatedOption.name = UPDATED_NAME
        updatedOption.price = UPDATED_PRICE
        updatedOption.sessionTime = UPDATED_SESSION_TIME
        updatedOption.active = UPDATED_ACTIVE

        restOptionMockMvc.perform(
            put("/api/options")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedOption))
        ).andExpect(status().isOk)

        // Validate the Option in the database
        val optionList = optionRepository.findAll()
        assertThat(optionList).hasSize(databaseSizeBeforeUpdate)
        val testOption = optionList[optionList.size - 1]
        assertThat(testOption.name).isEqualTo(UPDATED_NAME)
        assertThat(testOption.price).isEqualTo(UPDATED_PRICE)
        assertThat(testOption.sessionTime).isEqualTo(UPDATED_SESSION_TIME)
        assertThat(testOption.active).isEqualTo(UPDATED_ACTIVE)

        // Validate the Option in Elasticsearch
        verify(mockOptionSearchRepository, times(1)).save(testOption)
    }

    @Test
    @Transactional
    fun updateNonExistingOption() {
        val databaseSizeBeforeUpdate = optionRepository.findAll().size

        // Create the Option

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOptionMockMvc.perform(
            put("/api/options")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(option))
        ).andExpect(status().isBadRequest)

        // Validate the Option in the database
        val optionList = optionRepository.findAll()
        assertThat(optionList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Option in Elasticsearch
        verify(mockOptionSearchRepository, times(0)).save(option)
    }

    @Test
    @Transactional
    fun deleteOption() {
        // Initialize the database
        optionService.save(option)

        val databaseSizeBeforeDelete = optionRepository.findAll().size

        val id = option.id
        assertNotNull(id)

        // Delete the option
        restOptionMockMvc.perform(
            delete("/api/options/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val optionList = optionRepository.findAll()
        assertThat(optionList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Option in Elasticsearch
        verify(mockOptionSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchOption() {
        // Initialize the database
        optionService.save(option)
        `when`(mockOptionSearchRepository.search(queryStringQuery("id:" + option.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(option), PageRequest.of(0, 1), 1))
        // Search the option
        restOptionMockMvc.perform(get("/api/_search/options?query=id:" + option.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(option.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toInt())))
            .andExpect(jsonPath("$.[*].sessionTime").value(hasItem(DEFAULT_SESSION_TIME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(Option::class.java)
        val option1 = Option()
        option1.id = 1L
        val option2 = Option()
        option2.id = option1.id
        assertThat(option1).isEqualTo(option2)
        option2.id = 2L
        assertThat(option1).isNotEqualTo(option2)
        option1.id = null
        assertThat(option1).isNotEqualTo(option2)
    }

    companion object {

        private const val DEFAULT_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private val DEFAULT_PRICE: BigDecimal = BigDecimal(1)
        private val UPDATED_PRICE: BigDecimal = BigDecimal(2)

        private const val DEFAULT_SESSION_TIME: Int = 1
        private const val UPDATED_SESSION_TIME: Int = 2

        private const val DEFAULT_ACTIVE: Boolean = false
        private const val UPDATED_ACTIVE: Boolean = true
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Option {
            val option = Option()
            option.name = DEFAULT_NAME
            option.price = DEFAULT_PRICE
            option.sessionTime = DEFAULT_SESSION_TIME
            option.active = DEFAULT_ACTIVE

            return option
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Option {
            val option = Option()
            option.name = UPDATED_NAME
            option.price = UPDATED_PRICE
            option.sessionTime = UPDATED_SESSION_TIME
            option.active = UPDATED_ACTIVE

            return option
        }
    }
}
