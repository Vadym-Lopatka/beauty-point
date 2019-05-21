package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.Record
import com.beautypoint.app.domain.Salon
import com.beautypoint.app.domain.enumeration.OrderStatusEnum
import com.beautypoint.app.repository.RecordRepository
import com.beautypoint.app.repository.search.RecordSearchRepository
import com.beautypoint.app.service.RecordQueryService
import com.beautypoint.app.service.RecordService
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
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Test class for the RecordResource REST controller.
 *
 * @see RecordResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class RecordResourceIT {

    @Autowired
    private lateinit var recordRepository: RecordRepository

    @Mock
    private lateinit var recordRepositoryMock: RecordRepository

    @Mock
    private lateinit var recordServiceMock: RecordService

    @Autowired
    private lateinit var recordService: RecordService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.RecordSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockRecordSearchRepository: RecordSearchRepository

    @Autowired
    private lateinit var recordQueryService: RecordQueryService

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

    private lateinit var restRecordMockMvc: MockMvc

    private lateinit var record: Record

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val recordResource = RecordResource(recordService, recordQueryService)
        this.restRecordMockMvc = MockMvcBuilders.standaloneSetup(recordResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        record = createEntity(em)
    }

    @Test
    @Transactional
    fun createRecord() {
        val databaseSizeBeforeCreate = recordRepository.findAll().size

        // Create the Record
        restRecordMockMvc.perform(
            post("/api/records")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(record))
        ).andExpect(status().isCreated)

        // Validate the Record in the database
        val recordList = recordRepository.findAll()
        assertThat(recordList).hasSize(databaseSizeBeforeCreate + 1)
        val testRecord = recordList[recordList.size - 1]
        assertThat(testRecord.bookingTime).isEqualTo(DEFAULT_BOOKING_TIME)
        assertThat(testRecord.duration).isEqualTo(DEFAULT_DURATION)
        assertThat(testRecord.totalPrice).isEqualTo(DEFAULT_TOTAL_PRICE)
        assertThat(testRecord.orderStatus).isEqualTo(DEFAULT_ORDER_STATUS)
        assertThat(testRecord.comment).isEqualTo(DEFAULT_COMMENT)

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(1)).save(testRecord)
    }

    @Test
    @Transactional
    fun createRecordWithExistingId() {
        val databaseSizeBeforeCreate = recordRepository.findAll().size

        // Create the Record with an existing ID
        record.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecordMockMvc.perform(
            post("/api/records")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(record))
        ).andExpect(status().isBadRequest)

        // Validate the Record in the database
        val recordList = recordRepository.findAll()
        assertThat(recordList).hasSize(databaseSizeBeforeCreate)

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(0)).save(record)
    }


    @Test
    @Transactional
    fun checkBookingTimeIsRequired() {
        val databaseSizeBeforeTest = recordRepository.findAll().size
        // set the field null
        record.bookingTime = null

        // Create the Record, which fails.

        restRecordMockMvc.perform(
            post("/api/records")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(record))
        ).andExpect(status().isBadRequest)

        val recordList = recordRepository.findAll()
        assertThat(recordList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkDurationIsRequired() {
        val databaseSizeBeforeTest = recordRepository.findAll().size
        // set the field null
        record.duration = null

        // Create the Record, which fails.

        restRecordMockMvc.perform(
            post("/api/records")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(record))
        ).andExpect(status().isBadRequest)

        val recordList = recordRepository.findAll()
        assertThat(recordList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkTotalPriceIsRequired() {
        val databaseSizeBeforeTest = recordRepository.findAll().size
        // set the field null
        record.totalPrice = null

        // Create the Record, which fails.

        restRecordMockMvc.perform(
            post("/api/records")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(record))
        ).andExpect(status().isBadRequest)

        val recordList = recordRepository.findAll()
        assertThat(recordList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkOrderStatusIsRequired() {
        val databaseSizeBeforeTest = recordRepository.findAll().size
        // set the field null
        record.orderStatus = null

        // Create the Record, which fails.

        restRecordMockMvc.perform(
            post("/api/records")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(record))
        ).andExpect(status().isBadRequest)

        val recordList = recordRepository.findAll()
        assertThat(recordList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllRecords() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList
        restRecordMockMvc.perform(get("/api/records?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(record.id?.toInt())))
            .andExpect(jsonPath("$.[*].bookingTime").value(hasItem(DEFAULT_BOOKING_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.toInt())))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
    }

    @SuppressWarnings("unchecked")
    fun getAllRecordsWithEagerRelationshipsIsEnabled() {
        val recordResource = RecordResource(recordServiceMock, recordQueryService)
        `when`(recordServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restRecordMockMvc = MockMvcBuilders.standaloneSetup(recordResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restRecordMockMvc.perform(get("/api/records?eagerload=true"))
            .andExpect(status().isOk)

        verify(recordServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @SuppressWarnings("unchecked")
    fun getAllRecordsWithEagerRelationshipsIsNotEnabled() {
        val recordResource = RecordResource(recordServiceMock, recordQueryService)
        `when`(recordServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restRecordMockMvc = MockMvcBuilders.standaloneSetup(recordResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restRecordMockMvc.perform(get("/api/records?eagerload=true"))
            .andExpect(status().isOk)

        verify(recordServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getRecord() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        val id = record.id
        assertNotNull(id)

        // Get the record
        restRecordMockMvc.perform(get("/api/records/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.bookingTime").value(DEFAULT_BOOKING_TIME.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION))
            .andExpect(jsonPath("$.totalPrice").value(DEFAULT_TOTAL_PRICE.toInt()))
            .andExpect(jsonPath("$.orderStatus").value(DEFAULT_ORDER_STATUS.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
    }

    @Test
    @Transactional
    fun getAllRecordsByBookingTimeIsEqualToSomething() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where bookingTime equals to DEFAULT_BOOKING_TIME
        defaultRecordShouldBeFound("bookingTime.equals=$DEFAULT_BOOKING_TIME")

        // Get all the recordList where bookingTime equals to UPDATED_BOOKING_TIME
        defaultRecordShouldNotBeFound("bookingTime.equals=$UPDATED_BOOKING_TIME")
    }

    @Test
    @Transactional
    fun getAllRecordsByBookingTimeIsInShouldWork() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where bookingTime in DEFAULT_BOOKING_TIME or UPDATED_BOOKING_TIME
        defaultRecordShouldBeFound("bookingTime.in=$DEFAULT_BOOKING_TIME,$UPDATED_BOOKING_TIME")

        // Get all the recordList where bookingTime equals to UPDATED_BOOKING_TIME
        defaultRecordShouldNotBeFound("bookingTime.in=$UPDATED_BOOKING_TIME")
    }

    @Test
    @Transactional
    fun getAllRecordsByBookingTimeIsNullOrNotNull() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where bookingTime is not null
        defaultRecordShouldBeFound("bookingTime.specified=true")

        // Get all the recordList where bookingTime is null
        defaultRecordShouldNotBeFound("bookingTime.specified=false")
    }

    @Test
    @Transactional
    fun getAllRecordsByDurationIsEqualToSomething() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where duration equals to DEFAULT_DURATION
        defaultRecordShouldBeFound("duration.equals=$DEFAULT_DURATION")

        // Get all the recordList where duration equals to UPDATED_DURATION
        defaultRecordShouldNotBeFound("duration.equals=$UPDATED_DURATION")
    }

    @Test
    @Transactional
    fun getAllRecordsByDurationIsInShouldWork() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where duration in DEFAULT_DURATION or UPDATED_DURATION
        defaultRecordShouldBeFound("duration.in=$DEFAULT_DURATION,$UPDATED_DURATION")

        // Get all the recordList where duration equals to UPDATED_DURATION
        defaultRecordShouldNotBeFound("duration.in=$UPDATED_DURATION")
    }

    @Test
    @Transactional
    fun getAllRecordsByDurationIsNullOrNotNull() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where duration is not null
        defaultRecordShouldBeFound("duration.specified=true")

        // Get all the recordList where duration is null
        defaultRecordShouldNotBeFound("duration.specified=false")
    }

    @Test
    @Transactional
    fun getAllRecordsByDurationIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where duration greater than or equals to DEFAULT_DURATION
        defaultRecordShouldBeFound("duration.greaterOrEqualThan=$DEFAULT_DURATION")

        // Get all the recordList where duration greater than or equals to UPDATED_DURATION
        defaultRecordShouldNotBeFound("duration.greaterOrEqualThan=$UPDATED_DURATION")
    }

    @Test
    @Transactional
    fun getAllRecordsByDurationIsLessThanSomething() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where duration less than or equals to DEFAULT_DURATION
        defaultRecordShouldNotBeFound("duration.lessThan=$DEFAULT_DURATION")

        // Get all the recordList where duration less than or equals to UPDATED_DURATION
        defaultRecordShouldBeFound("duration.lessThan=$UPDATED_DURATION")
    }

    @Test
    @Transactional
    fun getAllRecordsByTotalPriceIsEqualToSomething() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where totalPrice equals to DEFAULT_TOTAL_PRICE
        defaultRecordShouldBeFound("totalPrice.equals=$DEFAULT_TOTAL_PRICE")

        // Get all the recordList where totalPrice equals to UPDATED_TOTAL_PRICE
        defaultRecordShouldNotBeFound("totalPrice.equals=$UPDATED_TOTAL_PRICE")
    }

    @Test
    @Transactional
    fun getAllRecordsByTotalPriceIsInShouldWork() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where totalPrice in DEFAULT_TOTAL_PRICE or UPDATED_TOTAL_PRICE
        defaultRecordShouldBeFound("totalPrice.in=$DEFAULT_TOTAL_PRICE,$UPDATED_TOTAL_PRICE")

        // Get all the recordList where totalPrice equals to UPDATED_TOTAL_PRICE
        defaultRecordShouldNotBeFound("totalPrice.in=$UPDATED_TOTAL_PRICE")
    }

    @Test
    @Transactional
    fun getAllRecordsByTotalPriceIsNullOrNotNull() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where totalPrice is not null
        defaultRecordShouldBeFound("totalPrice.specified=true")

        // Get all the recordList where totalPrice is null
        defaultRecordShouldNotBeFound("totalPrice.specified=false")
    }

    @Test
    @Transactional
    fun getAllRecordsByOrderStatusIsEqualToSomething() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where orderStatus equals to DEFAULT_ORDER_STATUS
        defaultRecordShouldBeFound("orderStatus.equals=$DEFAULT_ORDER_STATUS")

        // Get all the recordList where orderStatus equals to UPDATED_ORDER_STATUS
        defaultRecordShouldNotBeFound("orderStatus.equals=$UPDATED_ORDER_STATUS")
    }

    @Test
    @Transactional
    fun getAllRecordsByOrderStatusIsInShouldWork() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where orderStatus in DEFAULT_ORDER_STATUS or UPDATED_ORDER_STATUS
        defaultRecordShouldBeFound("orderStatus.in=$DEFAULT_ORDER_STATUS,$UPDATED_ORDER_STATUS")

        // Get all the recordList where orderStatus equals to UPDATED_ORDER_STATUS
        defaultRecordShouldNotBeFound("orderStatus.in=$UPDATED_ORDER_STATUS")
    }

    @Test
    @Transactional
    fun getAllRecordsByOrderStatusIsNullOrNotNull() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where orderStatus is not null
        defaultRecordShouldBeFound("orderStatus.specified=true")

        // Get all the recordList where orderStatus is null
        defaultRecordShouldNotBeFound("orderStatus.specified=false")
    }

    @Test
    @Transactional
    fun getAllRecordsByCommentIsEqualToSomething() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where comment equals to DEFAULT_COMMENT
        defaultRecordShouldBeFound("comment.equals=$DEFAULT_COMMENT")

        // Get all the recordList where comment equals to UPDATED_COMMENT
        defaultRecordShouldNotBeFound("comment.equals=$UPDATED_COMMENT")
    }

    @Test
    @Transactional
    fun getAllRecordsByCommentIsInShouldWork() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultRecordShouldBeFound("comment.in=$DEFAULT_COMMENT,$UPDATED_COMMENT")

        // Get all the recordList where comment equals to UPDATED_COMMENT
        defaultRecordShouldNotBeFound("comment.in=$UPDATED_COMMENT")
    }

    @Test
    @Transactional
    fun getAllRecordsByCommentIsNullOrNotNull() {
        // Initialize the database
        recordRepository.saveAndFlush(record)

        // Get all the recordList where comment is not null
        defaultRecordShouldBeFound("comment.specified=true")

        // Get all the recordList where comment is null
        defaultRecordShouldNotBeFound("comment.specified=false")
    }

    @Test
    @Transactional
    fun getAllRecordsByMasterIsEqualToSomething() {
        // Initialize the database
        val master = MasterResourceIT.createEntity(em)
        em.persist(master)
        em.flush()
        record.master = master
        recordRepository.saveAndFlush(record)
        val masterId = master.id

        // Get all the recordList where master equals to masterId
        defaultRecordShouldBeFound("masterId.equals=$masterId")

        // Get all the recordList where master equals to masterId + 1
        defaultRecordShouldNotBeFound("masterId.equals=${masterId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllRecordsByVariantIsEqualToSomething() {
        // Initialize the database
        val variant = VariantResourceIT.createEntity(em)
        em.persist(variant)
        em.flush()
        record.variant = variant
        recordRepository.saveAndFlush(record)
        val variantId = variant.id

        // Get all the recordList where variant equals to variantId
        defaultRecordShouldBeFound("variantId.equals=$variantId")

        // Get all the recordList where variant equals to variantId + 1
        defaultRecordShouldNotBeFound("variantId.equals=${variantId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllRecordsByOptionIsEqualToSomething() {
        // Initialize the database
        val option = OptionResourceIT.createEntity(em)
        em.persist(option)
        em.flush()
        record.addOption(option)
        recordRepository.saveAndFlush(record)
        val optionId = option.id

        // Get all the recordList where option equals to optionId
        defaultRecordShouldBeFound("optionId.equals=$optionId")

        // Get all the recordList where option equals to optionId + 1
        defaultRecordShouldNotBeFound("optionId.equals=${optionId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllRecordsByUserIsEqualToSomething() {
        // Initialize the database
        val user = UserResourceIT.createEntity(em)
        em.persist(user)
        em.flush()
        record.user = user
        recordRepository.saveAndFlush(record)
        val userId = user.id

        // Get all the recordList where user equals to userId
        defaultRecordShouldBeFound("userId.equals=$userId")

        // Get all the recordList where user equals to userId + 1
        defaultRecordShouldNotBeFound("userId.equals=${userId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllRecordsBySalonIsEqualToSomething() {
        // Initialize the database
        val salon = SalonResourceIT.createEntity(em)
        em.persist(salon)
        em.flush()
        record.salon = salon
        recordRepository.saveAndFlush(record)
        val salonId = salon.id

        // Get all the recordList where salon equals to salonId
        defaultRecordShouldBeFound("salonId.equals=$salonId")

        // Get all the recordList where salon equals to salonId + 1
        defaultRecordShouldNotBeFound("salonId.equals=${salonId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultRecordShouldBeFound(filter: String) {
        restRecordMockMvc.perform(get("/api/records?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(record.id?.toInt())))
            .andExpect(jsonPath("$.[*].bookingTime").value(hasItem(DEFAULT_BOOKING_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.toInt())))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))

        // Check, that the count call also returns 1
        restRecordMockMvc.perform(get("/api/records/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultRecordShouldNotBeFound(filter: String) {
        restRecordMockMvc.perform(get("/api/records?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restRecordMockMvc.perform(get("/api/records/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingRecord() {
        // Get the record
        restRecordMockMvc.perform(get("/api/records/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateRecord() {
        // Initialize the database
        recordService.save(record)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockRecordSearchRepository)

        val databaseSizeBeforeUpdate = recordRepository.findAll().size

        // Update the record
        val id = record.id
        assertNotNull(id)
        val updatedRecord = recordRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedRecord are not directly saved in db
        em.detach(updatedRecord)
        updatedRecord.bookingTime = UPDATED_BOOKING_TIME
        updatedRecord.duration = UPDATED_DURATION
        updatedRecord.totalPrice = UPDATED_TOTAL_PRICE
        updatedRecord.orderStatus = UPDATED_ORDER_STATUS
        updatedRecord.comment = UPDATED_COMMENT

        restRecordMockMvc.perform(
            put("/api/records")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRecord))
        ).andExpect(status().isOk)

        // Validate the Record in the database
        val recordList = recordRepository.findAll()
        assertThat(recordList).hasSize(databaseSizeBeforeUpdate)
        val testRecord = recordList[recordList.size - 1]
        assertThat(testRecord.bookingTime).isEqualTo(UPDATED_BOOKING_TIME)
        assertThat(testRecord.duration).isEqualTo(UPDATED_DURATION)
        assertThat(testRecord.totalPrice).isEqualTo(UPDATED_TOTAL_PRICE)
        assertThat(testRecord.orderStatus).isEqualTo(UPDATED_ORDER_STATUS)
        assertThat(testRecord.comment).isEqualTo(UPDATED_COMMENT)

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(1)).save(testRecord)
    }

    @Test
    @Transactional
    fun updateNonExistingRecord() {
        val databaseSizeBeforeUpdate = recordRepository.findAll().size

        // Create the Record

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecordMockMvc.perform(
            put("/api/records")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(record))
        ).andExpect(status().isBadRequest)

        // Validate the Record in the database
        val recordList = recordRepository.findAll()
        assertThat(recordList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(0)).save(record)
    }

    @Test
    @Transactional
    fun deleteRecord() {
        // Initialize the database
        recordService.save(record)

        val databaseSizeBeforeDelete = recordRepository.findAll().size

        val id = record.id
        assertNotNull(id)

        // Delete the record
        restRecordMockMvc.perform(
            delete("/api/records/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val recordList = recordRepository.findAll()
        assertThat(recordList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchRecord() {
        // Initialize the database
        recordService.save(record)
        `when`(mockRecordSearchRepository.search(queryStringQuery("id:" + record.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(record), PageRequest.of(0, 1), 1))
        // Search the record
        restRecordMockMvc.perform(get("/api/_search/records?query=id:" + record.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(record.id?.toInt())))
            .andExpect(jsonPath("$.[*].bookingTime").value(hasItem(DEFAULT_BOOKING_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.toInt())))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(Record::class.java)
        val record1 = Record()
        record1.id = 1L
        val record2 = Record()
        record2.id = record1.id
        assertThat(record1).isEqualTo(record2)
        record2.id = 2L
        assertThat(record1).isNotEqualTo(record2)
        record1.id = null
        assertThat(record1).isNotEqualTo(record2)
    }

    companion object {

        private val DEFAULT_BOOKING_TIME: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_BOOKING_TIME: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_DURATION: Int = 1
        private const val UPDATED_DURATION: Int = 2

        private val DEFAULT_TOTAL_PRICE: BigDecimal = BigDecimal(1)
        private val UPDATED_TOTAL_PRICE: BigDecimal = BigDecimal(2)

        private val DEFAULT_ORDER_STATUS: OrderStatusEnum = OrderStatusEnum.CREATED_BY_SALON
        private val UPDATED_ORDER_STATUS: OrderStatusEnum = OrderStatusEnum.CREATED_BY_CLIENT

        private const val DEFAULT_COMMENT: String = "AAAAAAAAAA"
        private const val UPDATED_COMMENT = "BBBBBBBBBB"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Record {
            val record = Record()
            record.bookingTime = DEFAULT_BOOKING_TIME
            record.duration = DEFAULT_DURATION
            record.totalPrice = DEFAULT_TOTAL_PRICE
            record.orderStatus = DEFAULT_ORDER_STATUS
            record.comment = DEFAULT_COMMENT

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            record.user = user
            // Add required entity
            val salon: Salon
            if (TestUtil.findAll(em, Salon::class.java).isEmpty()) {
                salon = SalonResourceIT.createEntity(em)
                em.persist(salon)
                em.flush()
            } else {
                salon = TestUtil.findAll(em, Salon::class.java).get(0)
            }
            record.salon = salon
            return record
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Record {
            val record = Record()
            record.bookingTime = UPDATED_BOOKING_TIME
            record.duration = UPDATED_DURATION
            record.totalPrice = UPDATED_TOTAL_PRICE
            record.orderStatus = UPDATED_ORDER_STATUS
            record.comment = UPDATED_COMMENT

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            record.user = user
            // Add required entity
            val salon: Salon
            if (TestUtil.findAll(em, Salon::class.java).isEmpty()) {
                salon = SalonResourceIT.createUpdatedEntity(em)
                em.persist(salon)
                em.flush()
            } else {
                salon = TestUtil.findAll(em, Salon::class.java).get(0)
            }
            record.salon = salon
            return record
        }
    }
}
