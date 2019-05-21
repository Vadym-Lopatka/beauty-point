package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.Variant
import com.beautypoint.app.repository.VariantRepository
import com.beautypoint.app.repository.search.VariantSearchRepository
import com.beautypoint.app.service.VariantQueryService
import com.beautypoint.app.service.VariantService
import com.beautypoint.app.web.rest.TestUtil.createFormattingConversionService
import com.beautypoint.app.web.rest.errors.ExceptionTranslator
import org.assertj.core.api.Assertions.assertThat
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
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
 * Test class for the VariantResource REST controller.
 *
 * @see VariantResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class VariantResourceIT {

    @Autowired
    private lateinit var variantRepository: VariantRepository

    @Mock
    private lateinit var variantRepositoryMock: VariantRepository

    @Mock
    private lateinit var variantServiceMock: VariantService

    @Autowired
    private lateinit var variantService: VariantService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.VariantSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockVariantSearchRepository: VariantSearchRepository

    @Autowired
    private lateinit var variantQueryService: VariantQueryService

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

    private lateinit var restVariantMockMvc: MockMvc

    private lateinit var variant: Variant

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val variantResource = VariantResource(variantService, variantQueryService)
        this.restVariantMockMvc = MockMvcBuilders.standaloneSetup(variantResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        variant = createEntity(em)
    }

    @Test
    @Transactional
    fun createVariant() {
        val databaseSizeBeforeCreate = variantRepository.findAll().size

        // Create the Variant
        restVariantMockMvc.perform(
            post("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant))
        ).andExpect(status().isCreated)

        // Validate the Variant in the database
        val variantList = variantRepository.findAll()
        assertThat(variantList).hasSize(databaseSizeBeforeCreate + 1)
        val testVariant = variantList[variantList.size - 1]
        assertThat(testVariant.name).isEqualTo(DEFAULT_NAME)
        assertThat(testVariant.price).isEqualTo(DEFAULT_PRICE)
        assertThat(testVariant.sessionTime).isEqualTo(DEFAULT_SESSION_TIME)
        assertThat(testVariant.active).isEqualTo(DEFAULT_ACTIVE)

        // Validate the Variant in Elasticsearch
        verify(mockVariantSearchRepository, times(1)).save(testVariant)
    }

    @Test
    @Transactional
    fun createVariantWithExistingId() {
        val databaseSizeBeforeCreate = variantRepository.findAll().size

        // Create the Variant with an existing ID
        variant.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restVariantMockMvc.perform(
            post("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant))
        ).andExpect(status().isBadRequest)

        // Validate the Variant in the database
        val variantList = variantRepository.findAll()
        assertThat(variantList).hasSize(databaseSizeBeforeCreate)

        // Validate the Variant in Elasticsearch
        verify(mockVariantSearchRepository, times(0)).save(variant)
    }


    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = variantRepository.findAll().size
        // set the field null
        variant.name = null

        // Create the Variant, which fails.

        restVariantMockMvc.perform(
            post("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant))
        ).andExpect(status().isBadRequest)

        val variantList = variantRepository.findAll()
        assertThat(variantList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPriceIsRequired() {
        val databaseSizeBeforeTest = variantRepository.findAll().size
        // set the field null
        variant.price = null

        // Create the Variant, which fails.

        restVariantMockMvc.perform(
            post("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant))
        ).andExpect(status().isBadRequest)

        val variantList = variantRepository.findAll()
        assertThat(variantList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkSessionTimeIsRequired() {
        val databaseSizeBeforeTest = variantRepository.findAll().size
        // set the field null
        variant.sessionTime = null

        // Create the Variant, which fails.

        restVariantMockMvc.perform(
            post("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant))
        ).andExpect(status().isBadRequest)

        val variantList = variantRepository.findAll()
        assertThat(variantList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkActiveIsRequired() {
        val databaseSizeBeforeTest = variantRepository.findAll().size
        // set the field null
        variant.active = null

        // Create the Variant, which fails.

        restVariantMockMvc.perform(
            post("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant))
        ).andExpect(status().isBadRequest)

        val variantList = variantRepository.findAll()
        assertThat(variantList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllVariants() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList
        restVariantMockMvc.perform(get("/api/variants?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(variant.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toInt())))
            .andExpect(jsonPath("$.[*].sessionTime").value(hasItem(DEFAULT_SESSION_TIME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
    }

    @SuppressWarnings("unchecked")
    fun getAllVariantsWithEagerRelationshipsIsEnabled() {
        val variantResource = VariantResource(variantServiceMock, variantQueryService)
        `when`(variantServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restVariantMockMvc = MockMvcBuilders.standaloneSetup(variantResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restVariantMockMvc.perform(get("/api/variants?eagerload=true"))
            .andExpect(status().isOk)

        verify(variantServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @SuppressWarnings("unchecked")
    fun getAllVariantsWithEagerRelationshipsIsNotEnabled() {
        val variantResource = VariantResource(variantServiceMock, variantQueryService)
        `when`(variantServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restVariantMockMvc = MockMvcBuilders.standaloneSetup(variantResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restVariantMockMvc.perform(get("/api/variants?eagerload=true"))
            .andExpect(status().isOk)

        verify(variantServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getVariant() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        val id = variant.id
        assertNotNull(id)

        // Get the variant
        restVariantMockMvc.perform(get("/api/variants/{id}", id))
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
    fun getAllVariantsByNameIsEqualToSomething() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where name equals to DEFAULT_NAME
        defaultVariantShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the variantList where name equals to UPDATED_NAME
        defaultVariantShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllVariantsByNameIsInShouldWork() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where name in DEFAULT_NAME or UPDATED_NAME
        defaultVariantShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the variantList where name equals to UPDATED_NAME
        defaultVariantShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllVariantsByNameIsNullOrNotNull() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where name is not null
        defaultVariantShouldBeFound("name.specified=true")

        // Get all the variantList where name is null
        defaultVariantShouldNotBeFound("name.specified=false")
    }

    @Test
    @Transactional
    fun getAllVariantsByPriceIsEqualToSomething() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where price equals to DEFAULT_PRICE
        defaultVariantShouldBeFound("price.equals=$DEFAULT_PRICE")

        // Get all the variantList where price equals to UPDATED_PRICE
        defaultVariantShouldNotBeFound("price.equals=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    fun getAllVariantsByPriceIsInShouldWork() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultVariantShouldBeFound("price.in=$DEFAULT_PRICE,$UPDATED_PRICE")

        // Get all the variantList where price equals to UPDATED_PRICE
        defaultVariantShouldNotBeFound("price.in=$UPDATED_PRICE")
    }

    @Test
    @Transactional
    fun getAllVariantsByPriceIsNullOrNotNull() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where price is not null
        defaultVariantShouldBeFound("price.specified=true")

        // Get all the variantList where price is null
        defaultVariantShouldNotBeFound("price.specified=false")
    }

    @Test
    @Transactional
    fun getAllVariantsBySessionTimeIsEqualToSomething() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where sessionTime equals to DEFAULT_SESSION_TIME
        defaultVariantShouldBeFound("sessionTime.equals=$DEFAULT_SESSION_TIME")

        // Get all the variantList where sessionTime equals to UPDATED_SESSION_TIME
        defaultVariantShouldNotBeFound("sessionTime.equals=$UPDATED_SESSION_TIME")
    }

    @Test
    @Transactional
    fun getAllVariantsBySessionTimeIsInShouldWork() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where sessionTime in DEFAULT_SESSION_TIME or UPDATED_SESSION_TIME
        defaultVariantShouldBeFound("sessionTime.in=$DEFAULT_SESSION_TIME,$UPDATED_SESSION_TIME")

        // Get all the variantList where sessionTime equals to UPDATED_SESSION_TIME
        defaultVariantShouldNotBeFound("sessionTime.in=$UPDATED_SESSION_TIME")
    }

    @Test
    @Transactional
    fun getAllVariantsBySessionTimeIsNullOrNotNull() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where sessionTime is not null
        defaultVariantShouldBeFound("sessionTime.specified=true")

        // Get all the variantList where sessionTime is null
        defaultVariantShouldNotBeFound("sessionTime.specified=false")
    }

    @Test
    @Transactional
    fun getAllVariantsBySessionTimeIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where sessionTime greater than or equals to DEFAULT_SESSION_TIME
        defaultVariantShouldBeFound("sessionTime.greaterOrEqualThan=$DEFAULT_SESSION_TIME")

        // Get all the variantList where sessionTime greater than or equals to UPDATED_SESSION_TIME
        defaultVariantShouldNotBeFound("sessionTime.greaterOrEqualThan=$UPDATED_SESSION_TIME")
    }

    @Test
    @Transactional
    fun getAllVariantsBySessionTimeIsLessThanSomething() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where sessionTime less than or equals to DEFAULT_SESSION_TIME
        defaultVariantShouldNotBeFound("sessionTime.lessThan=$DEFAULT_SESSION_TIME")

        // Get all the variantList where sessionTime less than or equals to UPDATED_SESSION_TIME
        defaultVariantShouldBeFound("sessionTime.lessThan=$UPDATED_SESSION_TIME")
    }

    @Test
    @Transactional
    fun getAllVariantsByActiveIsEqualToSomething() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where active equals to DEFAULT_ACTIVE
        defaultVariantShouldBeFound("active.equals=$DEFAULT_ACTIVE")

        // Get all the variantList where active equals to UPDATED_ACTIVE
        defaultVariantShouldNotBeFound("active.equals=$UPDATED_ACTIVE")
    }

    @Test
    @Transactional
    fun getAllVariantsByActiveIsInShouldWork() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultVariantShouldBeFound("active.in=$DEFAULT_ACTIVE,$UPDATED_ACTIVE")

        // Get all the variantList where active equals to UPDATED_ACTIVE
        defaultVariantShouldNotBeFound("active.in=$UPDATED_ACTIVE")
    }

    @Test
    @Transactional
    fun getAllVariantsByActiveIsNullOrNotNull() {
        // Initialize the database
        variantRepository.saveAndFlush(variant)

        // Get all the variantList where active is not null
        defaultVariantShouldBeFound("active.specified=true")

        // Get all the variantList where active is null
        defaultVariantShouldNotBeFound("active.specified=false")
    }

    @Test
    @Transactional
    fun getAllVariantsByOfferIsEqualToSomething() {
        // Initialize the database
        val offer = OfferResourceIT.createEntity(em)
        em.persist(offer)
        em.flush()
        variant.offer = offer
        variantRepository.saveAndFlush(variant)
        val offerId = offer.id

        // Get all the variantList where offer equals to offerId
        defaultVariantShouldBeFound("offerId.equals=$offerId")

        // Get all the variantList where offer equals to offerId + 1
        defaultVariantShouldNotBeFound("offerId.equals=${offerId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllVariantsByExecutorIsEqualToSomething() {
        // Initialize the database
        val executor = MasterResourceIT.createEntity(em)
        em.persist(executor)
        em.flush()
        variant.addExecutor(executor)
        variantRepository.saveAndFlush(variant)
        val executorId = executor.id

        // Get all the variantList where executor equals to executorId
        defaultVariantShouldBeFound("executorId.equals=$executorId")

        // Get all the variantList where executor equals to executorId + 1
        defaultVariantShouldNotBeFound("executorId.equals=${executorId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultVariantShouldBeFound(filter: String) {
        restVariantMockMvc.perform(get("/api/variants?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(variant.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toInt())))
            .andExpect(jsonPath("$.[*].sessionTime").value(hasItem(DEFAULT_SESSION_TIME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))

        // Check, that the count call also returns 1
        restVariantMockMvc.perform(get("/api/variants/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultVariantShouldNotBeFound(filter: String) {
        restVariantMockMvc.perform(get("/api/variants?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restVariantMockMvc.perform(get("/api/variants/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingVariant() {
        // Get the variant
        restVariantMockMvc.perform(get("/api/variants/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateVariant() {
        // Initialize the database
        variantService.save(variant)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockVariantSearchRepository)

        val databaseSizeBeforeUpdate = variantRepository.findAll().size

        // Update the variant
        val id = variant.id
        assertNotNull(id)
        val updatedVariant = variantRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedVariant are not directly saved in db
        em.detach(updatedVariant)
        updatedVariant.name = UPDATED_NAME
        updatedVariant.price = UPDATED_PRICE
        updatedVariant.sessionTime = UPDATED_SESSION_TIME
        updatedVariant.active = UPDATED_ACTIVE

        restVariantMockMvc.perform(
            put("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedVariant))
        ).andExpect(status().isOk)

        // Validate the Variant in the database
        val variantList = variantRepository.findAll()
        assertThat(variantList).hasSize(databaseSizeBeforeUpdate)
        val testVariant = variantList[variantList.size - 1]
        assertThat(testVariant.name).isEqualTo(UPDATED_NAME)
        assertThat(testVariant.price).isEqualTo(UPDATED_PRICE)
        assertThat(testVariant.sessionTime).isEqualTo(UPDATED_SESSION_TIME)
        assertThat(testVariant.active).isEqualTo(UPDATED_ACTIVE)

        // Validate the Variant in Elasticsearch
        verify(mockVariantSearchRepository, times(1)).save(testVariant)
    }

    @Test
    @Transactional
    fun updateNonExistingVariant() {
        val databaseSizeBeforeUpdate = variantRepository.findAll().size

        // Create the Variant

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVariantMockMvc.perform(
            put("/api/variants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(variant))
        ).andExpect(status().isBadRequest)

        // Validate the Variant in the database
        val variantList = variantRepository.findAll()
        assertThat(variantList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Variant in Elasticsearch
        verify(mockVariantSearchRepository, times(0)).save(variant)
    }

    @Test
    @Transactional
    fun deleteVariant() {
        // Initialize the database
        variantService.save(variant)

        val databaseSizeBeforeDelete = variantRepository.findAll().size

        val id = variant.id
        assertNotNull(id)

        // Delete the variant
        restVariantMockMvc.perform(
            delete("/api/variants/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val variantList = variantRepository.findAll()
        assertThat(variantList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Variant in Elasticsearch
        verify(mockVariantSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchVariant() {
        // Initialize the database
        variantService.save(variant)
        `when`(mockVariantSearchRepository.search(queryStringQuery("id:" + variant.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(variant), PageRequest.of(0, 1), 1))
        // Search the variant
        restVariantMockMvc.perform(get("/api/_search/variants?query=id:" + variant.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(variant.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toInt())))
            .andExpect(jsonPath("$.[*].sessionTime").value(hasItem(DEFAULT_SESSION_TIME)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(Variant::class.java)
        val variant1 = Variant()
        variant1.id = 1L
        val variant2 = Variant()
        variant2.id = variant1.id
        assertThat(variant1).isEqualTo(variant2)
        variant2.id = 2L
        assertThat(variant1).isNotEqualTo(variant2)
        variant1.id = null
        assertThat(variant1).isNotEqualTo(variant2)
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
        fun createEntity(em: EntityManager): Variant {
            val variant = Variant()
            variant.name = DEFAULT_NAME
            variant.price = DEFAULT_PRICE
            variant.sessionTime = DEFAULT_SESSION_TIME
            variant.active = DEFAULT_ACTIVE

            return variant
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Variant {
            val variant = Variant()
            variant.name = UPDATED_NAME
            variant.price = UPDATED_PRICE
            variant.sessionTime = UPDATED_SESSION_TIME
            variant.active = UPDATED_ACTIVE

            return variant
        }
    }
}
