package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.TimeTable
import com.beautypoint.app.repository.TimeTableRepository
import com.beautypoint.app.repository.search.TimeTableSearchRepository
import com.beautypoint.app.service.TimeTableQueryService
import com.beautypoint.app.service.TimeTableService
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
 * Test class for the TimeTableResource REST controller.
 *
 * @see TimeTableResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class TimeTableResourceIT {

    @Autowired
    private lateinit var timeTableRepository: TimeTableRepository

    @Autowired
    private lateinit var timeTableService: TimeTableService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.TimeTableSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockTimeTableSearchRepository: TimeTableSearchRepository

    @Autowired
    private lateinit var timeTableQueryService: TimeTableQueryService

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

    private lateinit var restTimeTableMockMvc: MockMvc

    private lateinit var timeTable: TimeTable

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val timeTableResource = TimeTableResource(timeTableService, timeTableQueryService)
        this.restTimeTableMockMvc = MockMvcBuilders.standaloneSetup(timeTableResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        timeTable = createEntity(em)
    }

    @Test
    @Transactional
    fun createTimeTable() {
        val databaseSizeBeforeCreate = timeTableRepository.findAll().size

        // Create the TimeTable
        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isCreated)

        // Validate the TimeTable in the database
        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeCreate + 1)
        val testTimeTable = timeTableList[timeTableList.size - 1]
        assertThat(testTimeTable.everyDayEqual).isEqualTo(DEFAULT_EVERY_DAY_EQUAL)
        assertThat(testTimeTable.mo).isEqualTo(DEFAULT_MO)
        assertThat(testTimeTable.tu).isEqualTo(DEFAULT_TU)
        assertThat(testTimeTable.we).isEqualTo(DEFAULT_WE)
        assertThat(testTimeTable.th).isEqualTo(DEFAULT_TH)
        assertThat(testTimeTable.fr).isEqualTo(DEFAULT_FR)
        assertThat(testTimeTable.sa).isEqualTo(DEFAULT_SA)
        assertThat(testTimeTable.su).isEqualTo(DEFAULT_SU)

        // Validate the TimeTable in Elasticsearch
        verify(mockTimeTableSearchRepository, times(1)).save(testTimeTable)
    }

    @Test
    @Transactional
    fun createTimeTableWithExistingId() {
        val databaseSizeBeforeCreate = timeTableRepository.findAll().size

        // Create the TimeTable with an existing ID
        timeTable.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        // Validate the TimeTable in the database
        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeCreate)

        // Validate the TimeTable in Elasticsearch
        verify(mockTimeTableSearchRepository, times(0)).save(timeTable)
    }


    @Test
    @Transactional
    fun checkEveryDayEqualIsRequired() {
        val databaseSizeBeforeTest = timeTableRepository.findAll().size
        // set the field null
        timeTable.everyDayEqual = null

        // Create the TimeTable, which fails.

        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkMoIsRequired() {
        val databaseSizeBeforeTest = timeTableRepository.findAll().size
        // set the field null
        timeTable.mo = null

        // Create the TimeTable, which fails.

        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkTuIsRequired() {
        val databaseSizeBeforeTest = timeTableRepository.findAll().size
        // set the field null
        timeTable.tu = null

        // Create the TimeTable, which fails.

        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkWeIsRequired() {
        val databaseSizeBeforeTest = timeTableRepository.findAll().size
        // set the field null
        timeTable.we = null

        // Create the TimeTable, which fails.

        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkThIsRequired() {
        val databaseSizeBeforeTest = timeTableRepository.findAll().size
        // set the field null
        timeTable.th = null

        // Create the TimeTable, which fails.

        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkFrIsRequired() {
        val databaseSizeBeforeTest = timeTableRepository.findAll().size
        // set the field null
        timeTable.fr = null

        // Create the TimeTable, which fails.

        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkSaIsRequired() {
        val databaseSizeBeforeTest = timeTableRepository.findAll().size
        // set the field null
        timeTable.sa = null

        // Create the TimeTable, which fails.

        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkSuIsRequired() {
        val databaseSizeBeforeTest = timeTableRepository.findAll().size
        // set the field null
        timeTable.su = null

        // Create the TimeTable, which fails.

        restTimeTableMockMvc.perform(
            post("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllTimeTables() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList
        restTimeTableMockMvc.perform(get("/api/time-tables?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeTable.id?.toInt())))
            .andExpect(jsonPath("$.[*].everyDayEqual").value(hasItem(DEFAULT_EVERY_DAY_EQUAL)))
            .andExpect(jsonPath("$.[*].mo").value(hasItem(DEFAULT_MO.toInt())))
            .andExpect(jsonPath("$.[*].tu").value(hasItem(DEFAULT_TU.toInt())))
            .andExpect(jsonPath("$.[*].we").value(hasItem(DEFAULT_WE.toInt())))
            .andExpect(jsonPath("$.[*].th").value(hasItem(DEFAULT_TH.toInt())))
            .andExpect(jsonPath("$.[*].fr").value(hasItem(DEFAULT_FR.toInt())))
            .andExpect(jsonPath("$.[*].sa").value(hasItem(DEFAULT_SA.toInt())))
            .andExpect(jsonPath("$.[*].su").value(hasItem(DEFAULT_SU.toInt())))
    }

    @Test
    @Transactional
    fun getTimeTable() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        val id = timeTable.id
        assertNotNull(id)

        // Get the timeTable
        restTimeTableMockMvc.perform(get("/api/time-tables/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.everyDayEqual").value(DEFAULT_EVERY_DAY_EQUAL))
            .andExpect(jsonPath("$.mo").value(DEFAULT_MO.toInt()))
            .andExpect(jsonPath("$.tu").value(DEFAULT_TU.toInt()))
            .andExpect(jsonPath("$.we").value(DEFAULT_WE.toInt()))
            .andExpect(jsonPath("$.th").value(DEFAULT_TH.toInt()))
            .andExpect(jsonPath("$.fr").value(DEFAULT_FR.toInt()))
            .andExpect(jsonPath("$.sa").value(DEFAULT_SA.toInt()))
            .andExpect(jsonPath("$.su").value(DEFAULT_SU.toInt()))
    }

    @Test
    @Transactional
    fun getAllTimeTablesByEveryDayEqualIsEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where everyDayEqual equals to DEFAULT_EVERY_DAY_EQUAL
        defaultTimeTableShouldBeFound("everyDayEqual.equals=$DEFAULT_EVERY_DAY_EQUAL")

        // Get all the timeTableList where everyDayEqual equals to UPDATED_EVERY_DAY_EQUAL
        defaultTimeTableShouldNotBeFound("everyDayEqual.equals=$UPDATED_EVERY_DAY_EQUAL")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByEveryDayEqualIsInShouldWork() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where everyDayEqual in DEFAULT_EVERY_DAY_EQUAL or UPDATED_EVERY_DAY_EQUAL
        defaultTimeTableShouldBeFound("everyDayEqual.in=$DEFAULT_EVERY_DAY_EQUAL,$UPDATED_EVERY_DAY_EQUAL")

        // Get all the timeTableList where everyDayEqual equals to UPDATED_EVERY_DAY_EQUAL
        defaultTimeTableShouldNotBeFound("everyDayEqual.in=$UPDATED_EVERY_DAY_EQUAL")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByEveryDayEqualIsNullOrNotNull() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where everyDayEqual is not null
        defaultTimeTableShouldBeFound("everyDayEqual.specified=true")

        // Get all the timeTableList where everyDayEqual is null
        defaultTimeTableShouldNotBeFound("everyDayEqual.specified=false")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByMoIsEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where mo equals to DEFAULT_MO
        defaultTimeTableShouldBeFound("mo.equals=$DEFAULT_MO")

        // Get all the timeTableList where mo equals to UPDATED_MO
        defaultTimeTableShouldNotBeFound("mo.equals=$UPDATED_MO")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByMoIsInShouldWork() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where mo in DEFAULT_MO or UPDATED_MO
        defaultTimeTableShouldBeFound("mo.in=$DEFAULT_MO,$UPDATED_MO")

        // Get all the timeTableList where mo equals to UPDATED_MO
        defaultTimeTableShouldNotBeFound("mo.in=$UPDATED_MO")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByMoIsNullOrNotNull() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where mo is not null
        defaultTimeTableShouldBeFound("mo.specified=true")

        // Get all the timeTableList where mo is null
        defaultTimeTableShouldNotBeFound("mo.specified=false")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByMoIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where mo greater than or equals to DEFAULT_MO
        defaultTimeTableShouldBeFound("mo.greaterOrEqualThan=$DEFAULT_MO")

        // Get all the timeTableList where mo greater than or equals to UPDATED_MO
        defaultTimeTableShouldNotBeFound("mo.greaterOrEqualThan=$UPDATED_MO")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByMoIsLessThanSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where mo less than or equals to DEFAULT_MO
        defaultTimeTableShouldNotBeFound("mo.lessThan=$DEFAULT_MO")

        // Get all the timeTableList where mo less than or equals to UPDATED_MO
        defaultTimeTableShouldBeFound("mo.lessThan=$UPDATED_MO")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByTuIsEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where tu equals to DEFAULT_TU
        defaultTimeTableShouldBeFound("tu.equals=$DEFAULT_TU")

        // Get all the timeTableList where tu equals to UPDATED_TU
        defaultTimeTableShouldNotBeFound("tu.equals=$UPDATED_TU")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByTuIsInShouldWork() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where tu in DEFAULT_TU or UPDATED_TU
        defaultTimeTableShouldBeFound("tu.in=$DEFAULT_TU,$UPDATED_TU")

        // Get all the timeTableList where tu equals to UPDATED_TU
        defaultTimeTableShouldNotBeFound("tu.in=$UPDATED_TU")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByTuIsNullOrNotNull() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where tu is not null
        defaultTimeTableShouldBeFound("tu.specified=true")

        // Get all the timeTableList where tu is null
        defaultTimeTableShouldNotBeFound("tu.specified=false")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByTuIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where tu greater than or equals to DEFAULT_TU
        defaultTimeTableShouldBeFound("tu.greaterOrEqualThan=$DEFAULT_TU")

        // Get all the timeTableList where tu greater than or equals to UPDATED_TU
        defaultTimeTableShouldNotBeFound("tu.greaterOrEqualThan=$UPDATED_TU")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByTuIsLessThanSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where tu less than or equals to DEFAULT_TU
        defaultTimeTableShouldNotBeFound("tu.lessThan=$DEFAULT_TU")

        // Get all the timeTableList where tu less than or equals to UPDATED_TU
        defaultTimeTableShouldBeFound("tu.lessThan=$UPDATED_TU")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByWeIsEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where we equals to DEFAULT_WE
        defaultTimeTableShouldBeFound("we.equals=$DEFAULT_WE")

        // Get all the timeTableList where we equals to UPDATED_WE
        defaultTimeTableShouldNotBeFound("we.equals=$UPDATED_WE")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByWeIsInShouldWork() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where we in DEFAULT_WE or UPDATED_WE
        defaultTimeTableShouldBeFound("we.in=$DEFAULT_WE,$UPDATED_WE")

        // Get all the timeTableList where we equals to UPDATED_WE
        defaultTimeTableShouldNotBeFound("we.in=$UPDATED_WE")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByWeIsNullOrNotNull() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where we is not null
        defaultTimeTableShouldBeFound("we.specified=true")

        // Get all the timeTableList where we is null
        defaultTimeTableShouldNotBeFound("we.specified=false")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByWeIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where we greater than or equals to DEFAULT_WE
        defaultTimeTableShouldBeFound("we.greaterOrEqualThan=$DEFAULT_WE")

        // Get all the timeTableList where we greater than or equals to UPDATED_WE
        defaultTimeTableShouldNotBeFound("we.greaterOrEqualThan=$UPDATED_WE")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByWeIsLessThanSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where we less than or equals to DEFAULT_WE
        defaultTimeTableShouldNotBeFound("we.lessThan=$DEFAULT_WE")

        // Get all the timeTableList where we less than or equals to UPDATED_WE
        defaultTimeTableShouldBeFound("we.lessThan=$UPDATED_WE")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByThIsEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where th equals to DEFAULT_TH
        defaultTimeTableShouldBeFound("th.equals=$DEFAULT_TH")

        // Get all the timeTableList where th equals to UPDATED_TH
        defaultTimeTableShouldNotBeFound("th.equals=$UPDATED_TH")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByThIsInShouldWork() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where th in DEFAULT_TH or UPDATED_TH
        defaultTimeTableShouldBeFound("th.in=$DEFAULT_TH,$UPDATED_TH")

        // Get all the timeTableList where th equals to UPDATED_TH
        defaultTimeTableShouldNotBeFound("th.in=$UPDATED_TH")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByThIsNullOrNotNull() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where th is not null
        defaultTimeTableShouldBeFound("th.specified=true")

        // Get all the timeTableList where th is null
        defaultTimeTableShouldNotBeFound("th.specified=false")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByThIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where th greater than or equals to DEFAULT_TH
        defaultTimeTableShouldBeFound("th.greaterOrEqualThan=$DEFAULT_TH")

        // Get all the timeTableList where th greater than or equals to UPDATED_TH
        defaultTimeTableShouldNotBeFound("th.greaterOrEqualThan=$UPDATED_TH")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByThIsLessThanSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where th less than or equals to DEFAULT_TH
        defaultTimeTableShouldNotBeFound("th.lessThan=$DEFAULT_TH")

        // Get all the timeTableList where th less than or equals to UPDATED_TH
        defaultTimeTableShouldBeFound("th.lessThan=$UPDATED_TH")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByFrIsEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where fr equals to DEFAULT_FR
        defaultTimeTableShouldBeFound("fr.equals=$DEFAULT_FR")

        // Get all the timeTableList where fr equals to UPDATED_FR
        defaultTimeTableShouldNotBeFound("fr.equals=$UPDATED_FR")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByFrIsInShouldWork() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where fr in DEFAULT_FR or UPDATED_FR
        defaultTimeTableShouldBeFound("fr.in=$DEFAULT_FR,$UPDATED_FR")

        // Get all the timeTableList where fr equals to UPDATED_FR
        defaultTimeTableShouldNotBeFound("fr.in=$UPDATED_FR")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByFrIsNullOrNotNull() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where fr is not null
        defaultTimeTableShouldBeFound("fr.specified=true")

        // Get all the timeTableList where fr is null
        defaultTimeTableShouldNotBeFound("fr.specified=false")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByFrIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where fr greater than or equals to DEFAULT_FR
        defaultTimeTableShouldBeFound("fr.greaterOrEqualThan=$DEFAULT_FR")

        // Get all the timeTableList where fr greater than or equals to UPDATED_FR
        defaultTimeTableShouldNotBeFound("fr.greaterOrEqualThan=$UPDATED_FR")
    }

    @Test
    @Transactional
    fun getAllTimeTablesByFrIsLessThanSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where fr less than or equals to DEFAULT_FR
        defaultTimeTableShouldNotBeFound("fr.lessThan=$DEFAULT_FR")

        // Get all the timeTableList where fr less than or equals to UPDATED_FR
        defaultTimeTableShouldBeFound("fr.lessThan=$UPDATED_FR")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySaIsEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where sa equals to DEFAULT_SA
        defaultTimeTableShouldBeFound("sa.equals=$DEFAULT_SA")

        // Get all the timeTableList where sa equals to UPDATED_SA
        defaultTimeTableShouldNotBeFound("sa.equals=$UPDATED_SA")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySaIsInShouldWork() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where sa in DEFAULT_SA or UPDATED_SA
        defaultTimeTableShouldBeFound("sa.in=$DEFAULT_SA,$UPDATED_SA")

        // Get all the timeTableList where sa equals to UPDATED_SA
        defaultTimeTableShouldNotBeFound("sa.in=$UPDATED_SA")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySaIsNullOrNotNull() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where sa is not null
        defaultTimeTableShouldBeFound("sa.specified=true")

        // Get all the timeTableList where sa is null
        defaultTimeTableShouldNotBeFound("sa.specified=false")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySaIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where sa greater than or equals to DEFAULT_SA
        defaultTimeTableShouldBeFound("sa.greaterOrEqualThan=$DEFAULT_SA")

        // Get all the timeTableList where sa greater than or equals to UPDATED_SA
        defaultTimeTableShouldNotBeFound("sa.greaterOrEqualThan=$UPDATED_SA")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySaIsLessThanSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where sa less than or equals to DEFAULT_SA
        defaultTimeTableShouldNotBeFound("sa.lessThan=$DEFAULT_SA")

        // Get all the timeTableList where sa less than or equals to UPDATED_SA
        defaultTimeTableShouldBeFound("sa.lessThan=$UPDATED_SA")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySuIsEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where su equals to DEFAULT_SU
        defaultTimeTableShouldBeFound("su.equals=$DEFAULT_SU")

        // Get all the timeTableList where su equals to UPDATED_SU
        defaultTimeTableShouldNotBeFound("su.equals=$UPDATED_SU")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySuIsInShouldWork() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where su in DEFAULT_SU or UPDATED_SU
        defaultTimeTableShouldBeFound("su.in=$DEFAULT_SU,$UPDATED_SU")

        // Get all the timeTableList where su equals to UPDATED_SU
        defaultTimeTableShouldNotBeFound("su.in=$UPDATED_SU")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySuIsNullOrNotNull() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where su is not null
        defaultTimeTableShouldBeFound("su.specified=true")

        // Get all the timeTableList where su is null
        defaultTimeTableShouldNotBeFound("su.specified=false")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySuIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where su greater than or equals to DEFAULT_SU
        defaultTimeTableShouldBeFound("su.greaterOrEqualThan=$DEFAULT_SU")

        // Get all the timeTableList where su greater than or equals to UPDATED_SU
        defaultTimeTableShouldNotBeFound("su.greaterOrEqualThan=$UPDATED_SU")
    }

    @Test
    @Transactional
    fun getAllTimeTablesBySuIsLessThanSomething() {
        // Initialize the database
        timeTableRepository.saveAndFlush(timeTable)

        // Get all the timeTableList where su less than or equals to DEFAULT_SU
        defaultTimeTableShouldNotBeFound("su.lessThan=$DEFAULT_SU")

        // Get all the timeTableList where su less than or equals to UPDATED_SU
        defaultTimeTableShouldBeFound("su.lessThan=$UPDATED_SU")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultTimeTableShouldBeFound(filter: String) {
        restTimeTableMockMvc.perform(get("/api/time-tables?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeTable.id?.toInt())))
            .andExpect(jsonPath("$.[*].everyDayEqual").value(hasItem(DEFAULT_EVERY_DAY_EQUAL)))
            .andExpect(jsonPath("$.[*].mo").value(hasItem(DEFAULT_MO.toInt())))
            .andExpect(jsonPath("$.[*].tu").value(hasItem(DEFAULT_TU.toInt())))
            .andExpect(jsonPath("$.[*].we").value(hasItem(DEFAULT_WE.toInt())))
            .andExpect(jsonPath("$.[*].th").value(hasItem(DEFAULT_TH.toInt())))
            .andExpect(jsonPath("$.[*].fr").value(hasItem(DEFAULT_FR.toInt())))
            .andExpect(jsonPath("$.[*].sa").value(hasItem(DEFAULT_SA.toInt())))
            .andExpect(jsonPath("$.[*].su").value(hasItem(DEFAULT_SU.toInt())))

        // Check, that the count call also returns 1
        restTimeTableMockMvc.perform(get("/api/time-tables/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultTimeTableShouldNotBeFound(filter: String) {
        restTimeTableMockMvc.perform(get("/api/time-tables?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restTimeTableMockMvc.perform(get("/api/time-tables/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingTimeTable() {
        // Get the timeTable
        restTimeTableMockMvc.perform(get("/api/time-tables/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateTimeTable() {
        // Initialize the database
        timeTableService.save(timeTable)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockTimeTableSearchRepository)

        val databaseSizeBeforeUpdate = timeTableRepository.findAll().size

        // Update the timeTable
        val id = timeTable.id
        assertNotNull(id)
        val updatedTimeTable = timeTableRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTimeTable are not directly saved in db
        em.detach(updatedTimeTable)
        updatedTimeTable.everyDayEqual = UPDATED_EVERY_DAY_EQUAL
        updatedTimeTable.mo = UPDATED_MO
        updatedTimeTable.tu = UPDATED_TU
        updatedTimeTable.we = UPDATED_WE
        updatedTimeTable.th = UPDATED_TH
        updatedTimeTable.fr = UPDATED_FR
        updatedTimeTable.sa = UPDATED_SA
        updatedTimeTable.su = UPDATED_SU

        restTimeTableMockMvc.perform(
            put("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTimeTable))
        ).andExpect(status().isOk)

        // Validate the TimeTable in the database
        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeUpdate)
        val testTimeTable = timeTableList[timeTableList.size - 1]
        assertThat(testTimeTable.everyDayEqual).isEqualTo(UPDATED_EVERY_DAY_EQUAL)
        assertThat(testTimeTable.mo).isEqualTo(UPDATED_MO)
        assertThat(testTimeTable.tu).isEqualTo(UPDATED_TU)
        assertThat(testTimeTable.we).isEqualTo(UPDATED_WE)
        assertThat(testTimeTable.th).isEqualTo(UPDATED_TH)
        assertThat(testTimeTable.fr).isEqualTo(UPDATED_FR)
        assertThat(testTimeTable.sa).isEqualTo(UPDATED_SA)
        assertThat(testTimeTable.su).isEqualTo(UPDATED_SU)

        // Validate the TimeTable in Elasticsearch
        verify(mockTimeTableSearchRepository, times(1)).save(testTimeTable)
    }

    @Test
    @Transactional
    fun updateNonExistingTimeTable() {
        val databaseSizeBeforeUpdate = timeTableRepository.findAll().size

        // Create the TimeTable

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeTableMockMvc.perform(
            put("/api/time-tables")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeTable))
        ).andExpect(status().isBadRequest)

        // Validate the TimeTable in the database
        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeUpdate)

        // Validate the TimeTable in Elasticsearch
        verify(mockTimeTableSearchRepository, times(0)).save(timeTable)
    }

    @Test
    @Transactional
    fun deleteTimeTable() {
        // Initialize the database
        timeTableService.save(timeTable)

        val databaseSizeBeforeDelete = timeTableRepository.findAll().size

        val id = timeTable.id
        assertNotNull(id)

        // Delete the timeTable
        restTimeTableMockMvc.perform(
            delete("/api/time-tables/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val timeTableList = timeTableRepository.findAll()
        assertThat(timeTableList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the TimeTable in Elasticsearch
        verify(mockTimeTableSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchTimeTable() {
        // Initialize the database
        timeTableService.save(timeTable)
        `when`(mockTimeTableSearchRepository.search(queryStringQuery("id:" + timeTable.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(timeTable), PageRequest.of(0, 1), 1))
        // Search the timeTable
        restTimeTableMockMvc.perform(get("/api/_search/time-tables?query=id:" + timeTable.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeTable.id?.toInt())))
            .andExpect(jsonPath("$.[*].everyDayEqual").value(hasItem(DEFAULT_EVERY_DAY_EQUAL)))
            .andExpect(jsonPath("$.[*].mo").value(hasItem(DEFAULT_MO.toInt())))
            .andExpect(jsonPath("$.[*].tu").value(hasItem(DEFAULT_TU.toInt())))
            .andExpect(jsonPath("$.[*].we").value(hasItem(DEFAULT_WE.toInt())))
            .andExpect(jsonPath("$.[*].th").value(hasItem(DEFAULT_TH.toInt())))
            .andExpect(jsonPath("$.[*].fr").value(hasItem(DEFAULT_FR.toInt())))
            .andExpect(jsonPath("$.[*].sa").value(hasItem(DEFAULT_SA.toInt())))
            .andExpect(jsonPath("$.[*].su").value(hasItem(DEFAULT_SU.toInt())))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(TimeTable::class.java)
        val timeTable1 = TimeTable()
        timeTable1.id = 1L
        val timeTable2 = TimeTable()
        timeTable2.id = timeTable1.id
        assertThat(timeTable1).isEqualTo(timeTable2)
        timeTable2.id = 2L
        assertThat(timeTable1).isNotEqualTo(timeTable2)
        timeTable1.id = null
        assertThat(timeTable1).isNotEqualTo(timeTable2)
    }

    companion object {

        private const val DEFAULT_EVERY_DAY_EQUAL: Boolean = false
        private const val UPDATED_EVERY_DAY_EQUAL: Boolean = true

        private const val DEFAULT_MO: Long = 1L
        private const val UPDATED_MO: Long = 2L

        private const val DEFAULT_TU: Long = 1L
        private const val UPDATED_TU: Long = 2L

        private const val DEFAULT_WE: Long = 1L
        private const val UPDATED_WE: Long = 2L

        private const val DEFAULT_TH: Long = 1L
        private const val UPDATED_TH: Long = 2L

        private const val DEFAULT_FR: Long = 1L
        private const val UPDATED_FR: Long = 2L

        private const val DEFAULT_SA: Long = 1L
        private const val UPDATED_SA: Long = 2L

        private const val DEFAULT_SU: Long = 1L
        private const val UPDATED_SU: Long = 2L
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): TimeTable {
            val timeTable = TimeTable()
            timeTable.everyDayEqual = DEFAULT_EVERY_DAY_EQUAL
            timeTable.mo = DEFAULT_MO
            timeTable.tu = DEFAULT_TU
            timeTable.we = DEFAULT_WE
            timeTable.th = DEFAULT_TH
            timeTable.fr = DEFAULT_FR
            timeTable.sa = DEFAULT_SA
            timeTable.su = DEFAULT_SU

            return timeTable
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): TimeTable {
            val timeTable = TimeTable()
            timeTable.everyDayEqual = UPDATED_EVERY_DAY_EQUAL
            timeTable.mo = UPDATED_MO
            timeTable.tu = UPDATED_TU
            timeTable.we = UPDATED_WE
            timeTable.th = UPDATED_TH
            timeTable.fr = UPDATED_FR
            timeTable.sa = UPDATED_SA
            timeTable.su = UPDATED_SU

            return timeTable
        }
    }
}
