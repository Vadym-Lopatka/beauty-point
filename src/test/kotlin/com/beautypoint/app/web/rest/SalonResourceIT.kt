package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.Salon
import com.beautypoint.app.domain.enumeration.SalonStatusEnum
import com.beautypoint.app.domain.enumeration.SalonTypeEnum
import com.beautypoint.app.repository.SalonRepository
import com.beautypoint.app.repository.search.SalonSearchRepository
import com.beautypoint.app.service.SalonQueryService
import com.beautypoint.app.service.SalonService
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
import java.util.*
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Test class for the SalonResource REST controller.
 *
 * @see SalonResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class SalonResourceIT {

    @Autowired
    private lateinit var salonRepository: SalonRepository

    @Mock
    private lateinit var salonRepositoryMock: SalonRepository

    @Mock
    private lateinit var salonServiceMock: SalonService

    @Autowired
    private lateinit var salonService: SalonService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.SalonSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockSalonSearchRepository: SalonSearchRepository

    @Autowired
    private lateinit var salonQueryService: SalonQueryService

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

    private lateinit var restSalonMockMvc: MockMvc

    private lateinit var salon: Salon

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val salonResource = SalonResource(salonService, salonQueryService)
        this.restSalonMockMvc = MockMvcBuilders.standaloneSetup(salonResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        salon = createEntity(em)
    }

    @Test
    @Transactional
    fun createSalon() {
        val databaseSizeBeforeCreate = salonRepository.findAll().size

        // Create the Salon
        restSalonMockMvc.perform(
            post("/api/salons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salon))
        ).andExpect(status().isCreated)

        // Validate the Salon in the database
        val salonList = salonRepository.findAll()
        assertThat(salonList).hasSize(databaseSizeBeforeCreate + 1)
        val testSalon = salonList[salonList.size - 1]
        assertThat(testSalon.name).isEqualTo(DEFAULT_NAME)
        assertThat(testSalon.slogan).isEqualTo(DEFAULT_SLOGAN)
        assertThat(testSalon.location).isEqualTo(DEFAULT_LOCATION)
        assertThat(testSalon.status).isEqualTo(DEFAULT_STATUS)
        assertThat(testSalon.systemComment).isEqualTo(DEFAULT_SYSTEM_COMMENT)
        assertThat(testSalon.type).isEqualTo(DEFAULT_TYPE)

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(1)).save(testSalon)
    }

    @Test
    @Transactional
    fun createSalonWithExistingId() {
        val databaseSizeBeforeCreate = salonRepository.findAll().size

        // Create the Salon with an existing ID
        salon.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalonMockMvc.perform(
            post("/api/salons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salon))
        ).andExpect(status().isBadRequest)

        // Validate the Salon in the database
        val salonList = salonRepository.findAll()
        assertThat(salonList).hasSize(databaseSizeBeforeCreate)

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(0)).save(salon)
    }


    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = salonRepository.findAll().size
        // set the field null
        salon.name = null

        // Create the Salon, which fails.

        restSalonMockMvc.perform(
            post("/api/salons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salon))
        ).andExpect(status().isBadRequest)

        val salonList = salonRepository.findAll()
        assertThat(salonList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkLocationIsRequired() {
        val databaseSizeBeforeTest = salonRepository.findAll().size
        // set the field null
        salon.location = null

        // Create the Salon, which fails.

        restSalonMockMvc.perform(
            post("/api/salons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salon))
        ).andExpect(status().isBadRequest)

        val salonList = salonRepository.findAll()
        assertThat(salonList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkStatusIsRequired() {
        val databaseSizeBeforeTest = salonRepository.findAll().size
        // set the field null
        salon.status = null

        // Create the Salon, which fails.

        restSalonMockMvc.perform(
            post("/api/salons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salon))
        ).andExpect(status().isBadRequest)

        val salonList = salonRepository.findAll()
        assertThat(salonList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkTypeIsRequired() {
        val databaseSizeBeforeTest = salonRepository.findAll().size
        // set the field null
        salon.type = null

        // Create the Salon, which fails.

        restSalonMockMvc.perform(
            post("/api/salons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salon))
        ).andExpect(status().isBadRequest)

        val salonList = salonRepository.findAll()
        assertThat(salonList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllSalons() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList
        restSalonMockMvc.perform(get("/api/salons?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salon.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].systemComment").value(hasItem(DEFAULT_SYSTEM_COMMENT)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
    }

    @SuppressWarnings("unchecked")
    fun getAllSalonsWithEagerRelationshipsIsEnabled() {
        val salonResource = SalonResource(salonServiceMock, salonQueryService)
        `when`(salonServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restSalonMockMvc = MockMvcBuilders.standaloneSetup(salonResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restSalonMockMvc.perform(get("/api/salons?eagerload=true"))
            .andExpect(status().isOk)

        verify(salonServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @SuppressWarnings("unchecked")
    fun getAllSalonsWithEagerRelationshipsIsNotEnabled() {
        val salonResource = SalonResource(salonServiceMock, salonQueryService)
        `when`(salonServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restSalonMockMvc = MockMvcBuilders.standaloneSetup(salonResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restSalonMockMvc.perform(get("/api/salons?eagerload=true"))
            .andExpect(status().isOk)

        verify(salonServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getSalon() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        val id = salon.id
        assertNotNull(id)

        // Get the salon
        restSalonMockMvc.perform(get("/api/salons/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slogan").value(DEFAULT_SLOGAN))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.systemComment").value(DEFAULT_SYSTEM_COMMENT))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
    }

    @Test
    @Transactional
    fun getAllSalonsByNameIsEqualToSomething() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where name equals to DEFAULT_NAME
        defaultSalonShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the salonList where name equals to UPDATED_NAME
        defaultSalonShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllSalonsByNameIsInShouldWork() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSalonShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the salonList where name equals to UPDATED_NAME
        defaultSalonShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllSalonsByNameIsNullOrNotNull() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where name is not null
        defaultSalonShouldBeFound("name.specified=true")

        // Get all the salonList where name is null
        defaultSalonShouldNotBeFound("name.specified=false")
    }

    @Test
    @Transactional
    fun getAllSalonsBySloganIsEqualToSomething() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where slogan equals to DEFAULT_SLOGAN
        defaultSalonShouldBeFound("slogan.equals=$DEFAULT_SLOGAN")

        // Get all the salonList where slogan equals to UPDATED_SLOGAN
        defaultSalonShouldNotBeFound("slogan.equals=$UPDATED_SLOGAN")
    }

    @Test
    @Transactional
    fun getAllSalonsBySloganIsInShouldWork() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where slogan in DEFAULT_SLOGAN or UPDATED_SLOGAN
        defaultSalonShouldBeFound("slogan.in=$DEFAULT_SLOGAN,$UPDATED_SLOGAN")

        // Get all the salonList where slogan equals to UPDATED_SLOGAN
        defaultSalonShouldNotBeFound("slogan.in=$UPDATED_SLOGAN")
    }

    @Test
    @Transactional
    fun getAllSalonsBySloganIsNullOrNotNull() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where slogan is not null
        defaultSalonShouldBeFound("slogan.specified=true")

        // Get all the salonList where slogan is null
        defaultSalonShouldNotBeFound("slogan.specified=false")
    }

    @Test
    @Transactional
    fun getAllSalonsByLocationIsEqualToSomething() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where location equals to DEFAULT_LOCATION
        defaultSalonShouldBeFound("location.equals=$DEFAULT_LOCATION")

        // Get all the salonList where location equals to UPDATED_LOCATION
        defaultSalonShouldNotBeFound("location.equals=$UPDATED_LOCATION")
    }

    @Test
    @Transactional
    fun getAllSalonsByLocationIsInShouldWork() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where location in DEFAULT_LOCATION or UPDATED_LOCATION
        defaultSalonShouldBeFound("location.in=$DEFAULT_LOCATION,$UPDATED_LOCATION")

        // Get all the salonList where location equals to UPDATED_LOCATION
        defaultSalonShouldNotBeFound("location.in=$UPDATED_LOCATION")
    }

    @Test
    @Transactional
    fun getAllSalonsByLocationIsNullOrNotNull() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where location is not null
        defaultSalonShouldBeFound("location.specified=true")

        // Get all the salonList where location is null
        defaultSalonShouldNotBeFound("location.specified=false")
    }

    @Test
    @Transactional
    fun getAllSalonsByStatusIsEqualToSomething() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where status equals to DEFAULT_STATUS
        defaultSalonShouldBeFound("status.equals=$DEFAULT_STATUS")

        // Get all the salonList where status equals to UPDATED_STATUS
        defaultSalonShouldNotBeFound("status.equals=$UPDATED_STATUS")
    }

    @Test
    @Transactional
    fun getAllSalonsByStatusIsInShouldWork() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultSalonShouldBeFound("status.in=$DEFAULT_STATUS,$UPDATED_STATUS")

        // Get all the salonList where status equals to UPDATED_STATUS
        defaultSalonShouldNotBeFound("status.in=$UPDATED_STATUS")
    }

    @Test
    @Transactional
    fun getAllSalonsByStatusIsNullOrNotNull() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where status is not null
        defaultSalonShouldBeFound("status.specified=true")

        // Get all the salonList where status is null
        defaultSalonShouldNotBeFound("status.specified=false")
    }

    @Test
    @Transactional
    fun getAllSalonsBySystemCommentIsEqualToSomething() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where systemComment equals to DEFAULT_SYSTEM_COMMENT
        defaultSalonShouldBeFound("systemComment.equals=$DEFAULT_SYSTEM_COMMENT")

        // Get all the salonList where systemComment equals to UPDATED_SYSTEM_COMMENT
        defaultSalonShouldNotBeFound("systemComment.equals=$UPDATED_SYSTEM_COMMENT")
    }

    @Test
    @Transactional
    fun getAllSalonsBySystemCommentIsInShouldWork() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where systemComment in DEFAULT_SYSTEM_COMMENT or UPDATED_SYSTEM_COMMENT
        defaultSalonShouldBeFound("systemComment.in=$DEFAULT_SYSTEM_COMMENT,$UPDATED_SYSTEM_COMMENT")

        // Get all the salonList where systemComment equals to UPDATED_SYSTEM_COMMENT
        defaultSalonShouldNotBeFound("systemComment.in=$UPDATED_SYSTEM_COMMENT")
    }

    @Test
    @Transactional
    fun getAllSalonsBySystemCommentIsNullOrNotNull() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where systemComment is not null
        defaultSalonShouldBeFound("systemComment.specified=true")

        // Get all the salonList where systemComment is null
        defaultSalonShouldNotBeFound("systemComment.specified=false")
    }

    @Test
    @Transactional
    fun getAllSalonsByTypeIsEqualToSomething() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where type equals to DEFAULT_TYPE
        defaultSalonShouldBeFound("type.equals=$DEFAULT_TYPE")

        // Get all the salonList where type equals to UPDATED_TYPE
        defaultSalonShouldNotBeFound("type.equals=$UPDATED_TYPE")
    }

    @Test
    @Transactional
    fun getAllSalonsByTypeIsInShouldWork() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultSalonShouldBeFound("type.in=$DEFAULT_TYPE,$UPDATED_TYPE")

        // Get all the salonList where type equals to UPDATED_TYPE
        defaultSalonShouldNotBeFound("type.in=$UPDATED_TYPE")
    }

    @Test
    @Transactional
    fun getAllSalonsByTypeIsNullOrNotNull() {
        // Initialize the database
        salonRepository.saveAndFlush(salon)

        // Get all the salonList where type is not null
        defaultSalonShouldBeFound("type.specified=true")

        // Get all the salonList where type is null
        defaultSalonShouldNotBeFound("type.specified=false")
    }

    @Test
    @Transactional
    fun getAllSalonsByImageIsEqualToSomething() {
        // Initialize the database
        val image = ImageResourceIT.createEntity(em)
        em.persist(image)
        em.flush()
        salon.image = image
        salonRepository.saveAndFlush(salon)
        val imageId = image.id

        // Get all the salonList where image equals to imageId
        defaultSalonShouldBeFound("imageId.equals=$imageId")

        // Get all the salonList where image equals to imageId + 1
        defaultSalonShouldNotBeFound("imageId.equals=${imageId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllSalonsByTimeTableIsEqualToSomething() {
        // Initialize the database
        val timeTable = TimeTableResourceIT.createEntity(em)
        em.persist(timeTable)
        em.flush()
        salon.timeTable = timeTable
        salonRepository.saveAndFlush(salon)
        val timeTableId = timeTable.id

        // Get all the salonList where timeTable equals to timeTableId
        defaultSalonShouldBeFound("timeTableId.equals=$timeTableId")

        // Get all the salonList where timeTable equals to timeTableId + 1
        defaultSalonShouldNotBeFound("timeTableId.equals=${timeTableId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllSalonsByOfferIsEqualToSomething() {
        // Initialize the database
        val offer = OfferResourceIT.createEntity(em)
        em.persist(offer)
        em.flush()
        salon.addOffer(offer)
        salonRepository.saveAndFlush(salon)
        val offerId = offer.id

        // Get all the salonList where offer equals to offerId
        defaultSalonShouldBeFound("offerId.equals=$offerId")

        // Get all the salonList where offer equals to offerId + 1
        defaultSalonShouldNotBeFound("offerId.equals=${offerId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllSalonsByMasterIsEqualToSomething() {
        // Initialize the database
        val master = MasterResourceIT.createEntity(em)
        em.persist(master)
        em.flush()
        salon.addMaster(master)
        salonRepository.saveAndFlush(salon)
        val masterId = master.id

        // Get all the salonList where master equals to masterId
        defaultSalonShouldBeFound("masterId.equals=$masterId")

        // Get all the salonList where master equals to masterId + 1
        defaultSalonShouldNotBeFound("masterId.equals=${masterId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllSalonsByCategoryIsEqualToSomething() {
        // Initialize the database
        val category = CategoryResourceIT.createEntity(em)
        em.persist(category)
        em.flush()
        salon.addCategory(category)
        salonRepository.saveAndFlush(salon)
        val categoryId = category.id

        // Get all the salonList where category equals to categoryId
        defaultSalonShouldBeFound("categoryId.equals=$categoryId")

        // Get all the salonList where category equals to categoryId + 1
        defaultSalonShouldNotBeFound("categoryId.equals=${categoryId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllSalonsByOwnerIsEqualToSomething() {
        // Initialize the database
        val owner = UserResourceIT.createEntity(em)
        em.persist(owner)
        em.flush()
        salon.owner = owner
        salonRepository.saveAndFlush(salon)
        val ownerId = owner.id

        // Get all the salonList where owner equals to ownerId
        defaultSalonShouldBeFound("ownerId.equals=$ownerId")

        // Get all the salonList where owner equals to ownerId + 1
        defaultSalonShouldNotBeFound("ownerId.equals=${ownerId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultSalonShouldBeFound(filter: String) {
        restSalonMockMvc.perform(get("/api/salons?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salon.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].systemComment").value(hasItem(DEFAULT_SYSTEM_COMMENT)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))

        // Check, that the count call also returns 1
        restSalonMockMvc.perform(get("/api/salons/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultSalonShouldNotBeFound(filter: String) {
        restSalonMockMvc.perform(get("/api/salons?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restSalonMockMvc.perform(get("/api/salons/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingSalon() {
        // Get the salon
        restSalonMockMvc.perform(get("/api/salons/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateSalon() {
        // Initialize the database
        salonService.save(salon)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSalonSearchRepository)

        val databaseSizeBeforeUpdate = salonRepository.findAll().size

        // Update the salon
        val id = salon.id
        assertNotNull(id)
        val updatedSalon = salonRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedSalon are not directly saved in db
        em.detach(updatedSalon)
        updatedSalon.name = UPDATED_NAME
        updatedSalon.slogan = UPDATED_SLOGAN
        updatedSalon.location = UPDATED_LOCATION
        updatedSalon.status = UPDATED_STATUS
        updatedSalon.systemComment = UPDATED_SYSTEM_COMMENT
        updatedSalon.type = UPDATED_TYPE

        restSalonMockMvc.perform(
            put("/api/salons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSalon))
        ).andExpect(status().isOk)

        // Validate the Salon in the database
        val salonList = salonRepository.findAll()
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate)
        val testSalon = salonList[salonList.size - 1]
        assertThat(testSalon.name).isEqualTo(UPDATED_NAME)
        assertThat(testSalon.slogan).isEqualTo(UPDATED_SLOGAN)
        assertThat(testSalon.location).isEqualTo(UPDATED_LOCATION)
        assertThat(testSalon.status).isEqualTo(UPDATED_STATUS)
        assertThat(testSalon.systemComment).isEqualTo(UPDATED_SYSTEM_COMMENT)
        assertThat(testSalon.type).isEqualTo(UPDATED_TYPE)

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(1)).save(testSalon)
    }

    @Test
    @Transactional
    fun updateNonExistingSalon() {
        val databaseSizeBeforeUpdate = salonRepository.findAll().size

        // Create the Salon

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalonMockMvc.perform(
            put("/api/salons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salon))
        ).andExpect(status().isBadRequest)

        // Validate the Salon in the database
        val salonList = salonRepository.findAll()
        assertThat(salonList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(0)).save(salon)
    }

    @Test
    @Transactional
    fun deleteSalon() {
        // Initialize the database
        salonService.save(salon)

        val databaseSizeBeforeDelete = salonRepository.findAll().size

        val id = salon.id
        assertNotNull(id)

        // Delete the salon
        restSalonMockMvc.perform(
            delete("/api/salons/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val salonList = salonRepository.findAll()
        assertThat(salonList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Salon in Elasticsearch
        verify(mockSalonSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchSalon() {
        // Initialize the database
        salonService.save(salon)
        `when`(mockSalonSearchRepository.search(queryStringQuery("id:" + salon.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(salon), PageRequest.of(0, 1), 1))
        // Search the salon
        restSalonMockMvc.perform(get("/api/_search/salons?query=id:" + salon.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salon.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].systemComment").value(hasItem(DEFAULT_SYSTEM_COMMENT)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(Salon::class.java)
        val salon1 = Salon()
        salon1.id = 1L
        val salon2 = Salon()
        salon2.id = salon1.id
        assertThat(salon1).isEqualTo(salon2)
        salon2.id = 2L
        assertThat(salon1).isNotEqualTo(salon2)
        salon1.id = null
        assertThat(salon1).isNotEqualTo(salon2)
    }

    companion object {

        private const val DEFAULT_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_SLOGAN: String = "AAAAAAAAAA"
        private const val UPDATED_SLOGAN = "BBBBBBBBBB"

        private const val DEFAULT_LOCATION: String = "AAAAAAAAAA"
        private const val UPDATED_LOCATION = "BBBBBBBBBB"

        private val DEFAULT_STATUS: SalonStatusEnum = SalonStatusEnum.EXAMPLE
        private val UPDATED_STATUS: SalonStatusEnum = SalonStatusEnum.DRAFT

        private const val DEFAULT_SYSTEM_COMMENT: String = "AAAAAAAAAA"
        private const val UPDATED_SYSTEM_COMMENT = "BBBBBBBBBB"

        private val DEFAULT_TYPE: SalonTypeEnum = SalonTypeEnum.STANDARD
        private val UPDATED_TYPE: SalonTypeEnum = SalonTypeEnum.PART_TIME_CUSTOM
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Salon {
            val salon = Salon()
            salon.name = DEFAULT_NAME
            salon.slogan = DEFAULT_SLOGAN
            salon.location = DEFAULT_LOCATION
            salon.status = DEFAULT_STATUS
            salon.systemComment = DEFAULT_SYSTEM_COMMENT
            salon.type = DEFAULT_TYPE

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            salon.owner = user
            return salon
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Salon {
            val salon = Salon()
            salon.name = UPDATED_NAME
            salon.slogan = UPDATED_SLOGAN
            salon.location = UPDATED_LOCATION
            salon.status = UPDATED_STATUS
            salon.systemComment = UPDATED_SYSTEM_COMMENT
            salon.type = UPDATED_TYPE

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            salon.owner = user
            return salon
        }
    }
}
