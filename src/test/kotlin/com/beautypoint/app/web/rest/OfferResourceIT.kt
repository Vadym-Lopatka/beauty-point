package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.Offer
import com.beautypoint.app.domain.enumeration.OfferStatusEnum
import com.beautypoint.app.repository.OfferRepository
import com.beautypoint.app.repository.search.OfferSearchRepository
import com.beautypoint.app.service.OfferQueryService
import com.beautypoint.app.service.OfferService
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
 * Test class for the OfferResource REST controller.
 *
 * @see OfferResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class OfferResourceIT {

    @Autowired
    private lateinit var offerRepository: OfferRepository

    @Autowired
    private lateinit var offerService: OfferService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.OfferSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockOfferSearchRepository: OfferSearchRepository

    @Autowired
    private lateinit var offerQueryService: OfferQueryService

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

    private lateinit var restOfferMockMvc: MockMvc

    private lateinit var offer: Offer

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val offerResource = OfferResource(offerService, offerQueryService)
        this.restOfferMockMvc = MockMvcBuilders.standaloneSetup(offerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        offer = createEntity(em)
    }

    @Test
    @Transactional
    fun createOffer() {
        val databaseSizeBeforeCreate = offerRepository.findAll().size

        // Create the Offer
        restOfferMockMvc.perform(
            post("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isCreated)

        // Validate the Offer in the database
        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeCreate + 1)
        val testOffer = offerList[offerList.size - 1]
        assertThat(testOffer.name).isEqualTo(DEFAULT_NAME)
        assertThat(testOffer.description).isEqualTo(DEFAULT_DESCRIPTION)
        assertThat(testOffer.priceLow).isEqualTo(DEFAULT_PRICE_LOW)
        assertThat(testOffer.priceHigh).isEqualTo(DEFAULT_PRICE_HIGH)
        assertThat(testOffer.active).isEqualTo(DEFAULT_ACTIVE)
        assertThat(testOffer.status).isEqualTo(DEFAULT_STATUS)

        // Validate the Offer in Elasticsearch
        verify(mockOfferSearchRepository, times(1)).save(testOffer)
    }

    @Test
    @Transactional
    fun createOfferWithExistingId() {
        val databaseSizeBeforeCreate = offerRepository.findAll().size

        // Create the Offer with an existing ID
        offer.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restOfferMockMvc.perform(
            post("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isBadRequest)

        // Validate the Offer in the database
        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeCreate)

        // Validate the Offer in Elasticsearch
        verify(mockOfferSearchRepository, times(0)).save(offer)
    }


    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = offerRepository.findAll().size
        // set the field null
        offer.name = null

        // Create the Offer, which fails.

        restOfferMockMvc.perform(
            post("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isBadRequest)

        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkDescriptionIsRequired() {
        val databaseSizeBeforeTest = offerRepository.findAll().size
        // set the field null
        offer.description = null

        // Create the Offer, which fails.

        restOfferMockMvc.perform(
            post("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isBadRequest)

        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPriceLowIsRequired() {
        val databaseSizeBeforeTest = offerRepository.findAll().size
        // set the field null
        offer.priceLow = null

        // Create the Offer, which fails.

        restOfferMockMvc.perform(
            post("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isBadRequest)

        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPriceHighIsRequired() {
        val databaseSizeBeforeTest = offerRepository.findAll().size
        // set the field null
        offer.priceHigh = null

        // Create the Offer, which fails.

        restOfferMockMvc.perform(
            post("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isBadRequest)

        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkActiveIsRequired() {
        val databaseSizeBeforeTest = offerRepository.findAll().size
        // set the field null
        offer.active = null

        // Create the Offer, which fails.

        restOfferMockMvc.perform(
            post("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isBadRequest)

        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkStatusIsRequired() {
        val databaseSizeBeforeTest = offerRepository.findAll().size
        // set the field null
        offer.status = null

        // Create the Offer, which fails.

        restOfferMockMvc.perform(
            post("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isBadRequest)

        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllOffers() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList
        restOfferMockMvc.perform(get("/api/offers?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(offer.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].priceLow").value(hasItem(DEFAULT_PRICE_LOW.toInt())))
            .andExpect(jsonPath("$.[*].priceHigh").value(hasItem(DEFAULT_PRICE_HIGH.toInt())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
    }

    @Test
    @Transactional
    fun getOffer() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        val id = offer.id
        assertNotNull(id)

        // Get the offer
        restOfferMockMvc.perform(get("/api/offers/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.priceLow").value(DEFAULT_PRICE_LOW.toInt()))
            .andExpect(jsonPath("$.priceHigh").value(DEFAULT_PRICE_HIGH.toInt()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
    }

    @Test
    @Transactional
    fun getAllOffersByNameIsEqualToSomething() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where name equals to DEFAULT_NAME
        defaultOfferShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the offerList where name equals to UPDATED_NAME
        defaultOfferShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllOffersByNameIsInShouldWork() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where name in DEFAULT_NAME or UPDATED_NAME
        defaultOfferShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the offerList where name equals to UPDATED_NAME
        defaultOfferShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllOffersByNameIsNullOrNotNull() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where name is not null
        defaultOfferShouldBeFound("name.specified=true")

        // Get all the offerList where name is null
        defaultOfferShouldNotBeFound("name.specified=false")
    }

    @Test
    @Transactional
    fun getAllOffersByDescriptionIsEqualToSomething() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where description equals to DEFAULT_DESCRIPTION
        defaultOfferShouldBeFound("description.equals=$DEFAULT_DESCRIPTION")

        // Get all the offerList where description equals to UPDATED_DESCRIPTION
        defaultOfferShouldNotBeFound("description.equals=$UPDATED_DESCRIPTION")
    }

    @Test
    @Transactional
    fun getAllOffersByDescriptionIsInShouldWork() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultOfferShouldBeFound("description.in=$DEFAULT_DESCRIPTION,$UPDATED_DESCRIPTION")

        // Get all the offerList where description equals to UPDATED_DESCRIPTION
        defaultOfferShouldNotBeFound("description.in=$UPDATED_DESCRIPTION")
    }

    @Test
    @Transactional
    fun getAllOffersByDescriptionIsNullOrNotNull() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where description is not null
        defaultOfferShouldBeFound("description.specified=true")

        // Get all the offerList where description is null
        defaultOfferShouldNotBeFound("description.specified=false")
    }

    @Test
    @Transactional
    fun getAllOffersByPriceLowIsEqualToSomething() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where priceLow equals to DEFAULT_PRICE_LOW
        defaultOfferShouldBeFound("priceLow.equals=$DEFAULT_PRICE_LOW")

        // Get all the offerList where priceLow equals to UPDATED_PRICE_LOW
        defaultOfferShouldNotBeFound("priceLow.equals=$UPDATED_PRICE_LOW")
    }

    @Test
    @Transactional
    fun getAllOffersByPriceLowIsInShouldWork() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where priceLow in DEFAULT_PRICE_LOW or UPDATED_PRICE_LOW
        defaultOfferShouldBeFound("priceLow.in=$DEFAULT_PRICE_LOW,$UPDATED_PRICE_LOW")

        // Get all the offerList where priceLow equals to UPDATED_PRICE_LOW
        defaultOfferShouldNotBeFound("priceLow.in=$UPDATED_PRICE_LOW")
    }

    @Test
    @Transactional
    fun getAllOffersByPriceLowIsNullOrNotNull() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where priceLow is not null
        defaultOfferShouldBeFound("priceLow.specified=true")

        // Get all the offerList where priceLow is null
        defaultOfferShouldNotBeFound("priceLow.specified=false")
    }

    @Test
    @Transactional
    fun getAllOffersByPriceHighIsEqualToSomething() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where priceHigh equals to DEFAULT_PRICE_HIGH
        defaultOfferShouldBeFound("priceHigh.equals=$DEFAULT_PRICE_HIGH")

        // Get all the offerList where priceHigh equals to UPDATED_PRICE_HIGH
        defaultOfferShouldNotBeFound("priceHigh.equals=$UPDATED_PRICE_HIGH")
    }

    @Test
    @Transactional
    fun getAllOffersByPriceHighIsInShouldWork() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where priceHigh in DEFAULT_PRICE_HIGH or UPDATED_PRICE_HIGH
        defaultOfferShouldBeFound("priceHigh.in=$DEFAULT_PRICE_HIGH,$UPDATED_PRICE_HIGH")

        // Get all the offerList where priceHigh equals to UPDATED_PRICE_HIGH
        defaultOfferShouldNotBeFound("priceHigh.in=$UPDATED_PRICE_HIGH")
    }

    @Test
    @Transactional
    fun getAllOffersByPriceHighIsNullOrNotNull() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where priceHigh is not null
        defaultOfferShouldBeFound("priceHigh.specified=true")

        // Get all the offerList where priceHigh is null
        defaultOfferShouldNotBeFound("priceHigh.specified=false")
    }

    @Test
    @Transactional
    fun getAllOffersByActiveIsEqualToSomething() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where active equals to DEFAULT_ACTIVE
        defaultOfferShouldBeFound("active.equals=$DEFAULT_ACTIVE")

        // Get all the offerList where active equals to UPDATED_ACTIVE
        defaultOfferShouldNotBeFound("active.equals=$UPDATED_ACTIVE")
    }

    @Test
    @Transactional
    fun getAllOffersByActiveIsInShouldWork() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultOfferShouldBeFound("active.in=$DEFAULT_ACTIVE,$UPDATED_ACTIVE")

        // Get all the offerList where active equals to UPDATED_ACTIVE
        defaultOfferShouldNotBeFound("active.in=$UPDATED_ACTIVE")
    }

    @Test
    @Transactional
    fun getAllOffersByActiveIsNullOrNotNull() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where active is not null
        defaultOfferShouldBeFound("active.specified=true")

        // Get all the offerList where active is null
        defaultOfferShouldNotBeFound("active.specified=false")
    }

    @Test
    @Transactional
    fun getAllOffersByStatusIsEqualToSomething() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where status equals to DEFAULT_STATUS
        defaultOfferShouldBeFound("status.equals=$DEFAULT_STATUS")

        // Get all the offerList where status equals to UPDATED_STATUS
        defaultOfferShouldNotBeFound("status.equals=$UPDATED_STATUS")
    }

    @Test
    @Transactional
    fun getAllOffersByStatusIsInShouldWork() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultOfferShouldBeFound("status.in=$DEFAULT_STATUS,$UPDATED_STATUS")

        // Get all the offerList where status equals to UPDATED_STATUS
        defaultOfferShouldNotBeFound("status.in=$UPDATED_STATUS")
    }

    @Test
    @Transactional
    fun getAllOffersByStatusIsNullOrNotNull() {
        // Initialize the database
        offerRepository.saveAndFlush(offer)

        // Get all the offerList where status is not null
        defaultOfferShouldBeFound("status.specified=true")

        // Get all the offerList where status is null
        defaultOfferShouldNotBeFound("status.specified=false")
    }

    @Test
    @Transactional
    fun getAllOffersBySalonIsEqualToSomething() {
        // Initialize the database
        val salon = SalonResourceIT.createEntity(em)
        em.persist(salon)
        em.flush()
        offer.salon = salon
        offerRepository.saveAndFlush(offer)
        val salonId = salon.id

        // Get all the offerList where salon equals to salonId
        defaultOfferShouldBeFound("salonId.equals=$salonId")

        // Get all the offerList where salon equals to salonId + 1
        defaultOfferShouldNotBeFound("salonId.equals=${salonId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllOffersByImageIsEqualToSomething() {
        // Initialize the database
        val image = ImageResourceIT.createEntity(em)
        em.persist(image)
        em.flush()
        offer.image = image
        offerRepository.saveAndFlush(offer)
        val imageId = image.id

        // Get all the offerList where image equals to imageId
        defaultOfferShouldBeFound("imageId.equals=$imageId")

        // Get all the offerList where image equals to imageId + 1
        defaultOfferShouldNotBeFound("imageId.equals=${imageId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllOffersByVariantIsEqualToSomething() {
        // Initialize the database
        val variant = VariantResourceIT.createEntity(em)
        em.persist(variant)
        em.flush()
        offer.addVariant(variant)
        offerRepository.saveAndFlush(offer)
        val variantId = variant.id

        // Get all the offerList where variant equals to variantId
        defaultOfferShouldBeFound("variantId.equals=$variantId")

        // Get all the offerList where variant equals to variantId + 1
        defaultOfferShouldNotBeFound("variantId.equals=${variantId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllOffersByOptionIsEqualToSomething() {
        // Initialize the database
        val option = OptionResourceIT.createEntity(em)
        em.persist(option)
        em.flush()
        offer.addOption(option)
        offerRepository.saveAndFlush(offer)
        val optionId = option.id

        // Get all the offerList where option equals to optionId
        defaultOfferShouldBeFound("optionId.equals=$optionId")

        // Get all the offerList where option equals to optionId + 1
        defaultOfferShouldNotBeFound("optionId.equals=${optionId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllOffersByCategoryIsEqualToSomething() {
        // Initialize the database
        val category = CategoryResourceIT.createEntity(em)
        em.persist(category)
        em.flush()
        offer.category = category
        offerRepository.saveAndFlush(offer)
        val categoryId = category.id

        // Get all the offerList where category equals to categoryId
        defaultOfferShouldBeFound("categoryId.equals=$categoryId")

        // Get all the offerList where category equals to categoryId + 1
        defaultOfferShouldNotBeFound("categoryId.equals=${categoryId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultOfferShouldBeFound(filter: String) {
        restOfferMockMvc.perform(get("/api/offers?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(offer.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].priceLow").value(hasItem(DEFAULT_PRICE_LOW.toInt())))
            .andExpect(jsonPath("$.[*].priceHigh").value(hasItem(DEFAULT_PRICE_HIGH.toInt())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))

        // Check, that the count call also returns 1
        restOfferMockMvc.perform(get("/api/offers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultOfferShouldNotBeFound(filter: String) {
        restOfferMockMvc.perform(get("/api/offers?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restOfferMockMvc.perform(get("/api/offers/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingOffer() {
        // Get the offer
        restOfferMockMvc.perform(get("/api/offers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateOffer() {
        // Initialize the database
        offerService.save(offer)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockOfferSearchRepository)

        val databaseSizeBeforeUpdate = offerRepository.findAll().size

        // Update the offer
        val id = offer.id
        assertNotNull(id)
        val updatedOffer = offerRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedOffer are not directly saved in db
        em.detach(updatedOffer)
        updatedOffer.name = UPDATED_NAME
        updatedOffer.description = UPDATED_DESCRIPTION
        updatedOffer.priceLow = UPDATED_PRICE_LOW
        updatedOffer.priceHigh = UPDATED_PRICE_HIGH
        updatedOffer.active = UPDATED_ACTIVE
        updatedOffer.status = UPDATED_STATUS

        restOfferMockMvc.perform(
            put("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedOffer))
        ).andExpect(status().isOk)

        // Validate the Offer in the database
        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeUpdate)
        val testOffer = offerList[offerList.size - 1]
        assertThat(testOffer.name).isEqualTo(UPDATED_NAME)
        assertThat(testOffer.description).isEqualTo(UPDATED_DESCRIPTION)
        assertThat(testOffer.priceLow).isEqualTo(UPDATED_PRICE_LOW)
        assertThat(testOffer.priceHigh).isEqualTo(UPDATED_PRICE_HIGH)
        assertThat(testOffer.active).isEqualTo(UPDATED_ACTIVE)
        assertThat(testOffer.status).isEqualTo(UPDATED_STATUS)

        // Validate the Offer in Elasticsearch
        verify(mockOfferSearchRepository, times(1)).save(testOffer)
    }

    @Test
    @Transactional
    fun updateNonExistingOffer() {
        val databaseSizeBeforeUpdate = offerRepository.findAll().size

        // Create the Offer

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOfferMockMvc.perform(
            put("/api/offers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isBadRequest)

        // Validate the Offer in the database
        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Offer in Elasticsearch
        verify(mockOfferSearchRepository, times(0)).save(offer)
    }

    @Test
    @Transactional
    fun deleteOffer() {
        // Initialize the database
        offerService.save(offer)

        val databaseSizeBeforeDelete = offerRepository.findAll().size

        val id = offer.id
        assertNotNull(id)

        // Delete the offer
        restOfferMockMvc.perform(
            delete("/api/offers/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val offerList = offerRepository.findAll()
        assertThat(offerList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Offer in Elasticsearch
        verify(mockOfferSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchOffer() {
        // Initialize the database
        offerService.save(offer)
        `when`(mockOfferSearchRepository.search(queryStringQuery("id:" + offer.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(offer), PageRequest.of(0, 1), 1))
        // Search the offer
        restOfferMockMvc.perform(get("/api/_search/offers?query=id:" + offer.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(offer.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].priceLow").value(hasItem(DEFAULT_PRICE_LOW.toInt())))
            .andExpect(jsonPath("$.[*].priceHigh").value(hasItem(DEFAULT_PRICE_HIGH.toInt())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(Offer::class.java)
        val offer1 = Offer()
        offer1.id = 1L
        val offer2 = Offer()
        offer2.id = offer1.id
        assertThat(offer1).isEqualTo(offer2)
        offer2.id = 2L
        assertThat(offer1).isNotEqualTo(offer2)
        offer1.id = null
        assertThat(offer1).isNotEqualTo(offer2)
    }

    companion object {

        private const val DEFAULT_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION: String = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private val DEFAULT_PRICE_LOW: BigDecimal = BigDecimal(1)
        private val UPDATED_PRICE_LOW: BigDecimal = BigDecimal(2)

        private val DEFAULT_PRICE_HIGH: BigDecimal = BigDecimal(1)
        private val UPDATED_PRICE_HIGH: BigDecimal = BigDecimal(2)

        private const val DEFAULT_ACTIVE: Boolean = false
        private const val UPDATED_ACTIVE: Boolean = true

        private val DEFAULT_STATUS: OfferStatusEnum = OfferStatusEnum.NORMAL
        private val UPDATED_STATUS: OfferStatusEnum = OfferStatusEnum.DELETED
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Offer {
            val offer = Offer()
            offer.name = DEFAULT_NAME
            offer.description = DEFAULT_DESCRIPTION
            offer.priceLow = DEFAULT_PRICE_LOW
            offer.priceHigh = DEFAULT_PRICE_HIGH
            offer.active = DEFAULT_ACTIVE
            offer.status = DEFAULT_STATUS

            return offer
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Offer {
            val offer = Offer()
            offer.name = UPDATED_NAME
            offer.description = UPDATED_DESCRIPTION
            offer.priceLow = UPDATED_PRICE_LOW
            offer.priceHigh = UPDATED_PRICE_HIGH
            offer.active = UPDATED_ACTIVE
            offer.status = UPDATED_STATUS

            return offer
        }
    }
}
