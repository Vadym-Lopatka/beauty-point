package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.Master
import com.beautypoint.app.repository.MasterRepository
import com.beautypoint.app.repository.search.MasterSearchRepository
import com.beautypoint.app.service.MasterQueryService
import com.beautypoint.app.service.MasterService
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
 * Test class for the MasterResource REST controller.
 *
 * @see MasterResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class MasterResourceIT {

    @Autowired
    private lateinit var masterRepository: MasterRepository

    @Mock
    private lateinit var masterRepositoryMock: MasterRepository

    @Mock
    private lateinit var masterServiceMock: MasterService

    @Autowired
    private lateinit var masterService: MasterService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.MasterSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockMasterSearchRepository: MasterSearchRepository

    @Autowired
    private lateinit var masterQueryService: MasterQueryService

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

    private lateinit var restMasterMockMvc: MockMvc

    private lateinit var master: Master

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val masterResource = MasterResource(masterService, masterQueryService)
        this.restMasterMockMvc = MockMvcBuilders.standaloneSetup(masterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        master = createEntity(em)
    }

    @Test
    @Transactional
    fun createMaster() {
        val databaseSizeBeforeCreate = masterRepository.findAll().size

        // Create the Master
        restMasterMockMvc.perform(
            post("/api/masters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(master))
        ).andExpect(status().isCreated)

        // Validate the Master in the database
        val masterList = masterRepository.findAll()
        assertThat(masterList).hasSize(databaseSizeBeforeCreate + 1)
        val testMaster = masterList[masterList.size - 1]

        // Validate the Master in Elasticsearch
        verify(mockMasterSearchRepository, times(1)).save(testMaster)
    }

    @Test
    @Transactional
    fun createMasterWithExistingId() {
        val databaseSizeBeforeCreate = masterRepository.findAll().size

        // Create the Master with an existing ID
        master.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restMasterMockMvc.perform(
            post("/api/masters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(master))
        ).andExpect(status().isBadRequest)

        // Validate the Master in the database
        val masterList = masterRepository.findAll()
        assertThat(masterList).hasSize(databaseSizeBeforeCreate)

        // Validate the Master in Elasticsearch
        verify(mockMasterSearchRepository, times(0)).save(master)
    }


    @Test
    @Transactional
    fun getAllMasters() {
        // Initialize the database
        masterRepository.saveAndFlush(master)

        // Get all the masterList
        restMasterMockMvc.perform(get("/api/masters?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(master.id?.toInt())))
    }

    @SuppressWarnings("unchecked")
    fun getAllMastersWithEagerRelationshipsIsEnabled() {
        val masterResource = MasterResource(masterServiceMock, masterQueryService)
        `when`(masterServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        val restMasterMockMvc = MockMvcBuilders.standaloneSetup(masterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restMasterMockMvc.perform(get("/api/masters?eagerload=true"))
            .andExpect(status().isOk)

        verify(masterServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @SuppressWarnings("unchecked")
    fun getAllMastersWithEagerRelationshipsIsNotEnabled() {
        val masterResource = MasterResource(masterServiceMock, masterQueryService)
        `when`(masterServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))
        val restMasterMockMvc = MockMvcBuilders.standaloneSetup(masterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build()

        restMasterMockMvc.perform(get("/api/masters?eagerload=true"))
            .andExpect(status().isOk)

        verify(masterServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    fun getMaster() {
        // Initialize the database
        masterRepository.saveAndFlush(master)

        val id = master.id
        assertNotNull(id)

        // Get the master
        restMasterMockMvc.perform(get("/api/masters/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
    }

    @Test
    @Transactional
    fun getAllMastersBySalonIsEqualToSomething() {
        // Initialize the database
        val salon = SalonResourceIT.createEntity(em)
        em.persist(salon)
        em.flush()
        master.salon = salon
        masterRepository.saveAndFlush(master)
        val salonId = salon.id

        // Get all the masterList where salon equals to salonId
        defaultMasterShouldBeFound("salonId.equals=$salonId")

        // Get all the masterList where salon equals to salonId + 1
        defaultMasterShouldNotBeFound("salonId.equals=${salonId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllMastersByRecordIsEqualToSomething() {
        // Initialize the database
        val record = RecordResourceIT.createEntity(em)
        em.persist(record)
        em.flush()
        master.addRecord(record)
        masterRepository.saveAndFlush(master)
        val recordId = record.id

        // Get all the masterList where record equals to recordId
        defaultMasterShouldBeFound("recordId.equals=$recordId")

        // Get all the masterList where record equals to recordId + 1
        defaultMasterShouldNotBeFound("recordId.equals=${recordId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllMastersByCategoryIsEqualToSomething() {
        // Initialize the database
        val category = CategoryResourceIT.createEntity(em)
        em.persist(category)
        em.flush()
        master.addCategory(category)
        masterRepository.saveAndFlush(master)
        val categoryId = category.id

        // Get all the masterList where category equals to categoryId
        defaultMasterShouldBeFound("categoryId.equals=$categoryId")

        // Get all the masterList where category equals to categoryId + 1
        defaultMasterShouldNotBeFound("categoryId.equals=${categoryId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllMastersByUserIsEqualToSomething() {
        // Initialize the database
        val user = UserResourceIT.createEntity(em)
        em.persist(user)
        em.flush()
        master.user = user
        masterRepository.saveAndFlush(master)
        val userId = user.id

        // Get all the masterList where user equals to userId
        defaultMasterShouldBeFound("userId.equals=$userId")

        // Get all the masterList where user equals to userId + 1
        defaultMasterShouldNotBeFound("userId.equals=${userId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultMasterShouldBeFound(filter: String) {
        restMasterMockMvc.perform(get("/api/masters?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(master.id?.toInt())))

        // Check, that the count call also returns 1
        restMasterMockMvc.perform(get("/api/masters/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultMasterShouldNotBeFound(filter: String) {
        restMasterMockMvc.perform(get("/api/masters?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restMasterMockMvc.perform(get("/api/masters/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingMaster() {
        // Get the master
        restMasterMockMvc.perform(get("/api/masters/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateMaster() {
        // Initialize the database
        masterService.save(master)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockMasterSearchRepository)

        val databaseSizeBeforeUpdate = masterRepository.findAll().size

        // Update the master
        val id = master.id
        assertNotNull(id)
        val updatedMaster = masterRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMaster are not directly saved in db
        em.detach(updatedMaster)

        restMasterMockMvc.perform(
            put("/api/masters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMaster))
        ).andExpect(status().isOk)

        // Validate the Master in the database
        val masterList = masterRepository.findAll()
        assertThat(masterList).hasSize(databaseSizeBeforeUpdate)
        val testMaster = masterList[masterList.size - 1]

        // Validate the Master in Elasticsearch
        verify(mockMasterSearchRepository, times(1)).save(testMaster)
    }

    @Test
    @Transactional
    fun updateNonExistingMaster() {
        val databaseSizeBeforeUpdate = masterRepository.findAll().size

        // Create the Master

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMasterMockMvc.perform(
            put("/api/masters")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(master))
        ).andExpect(status().isBadRequest)

        // Validate the Master in the database
        val masterList = masterRepository.findAll()
        assertThat(masterList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Master in Elasticsearch
        verify(mockMasterSearchRepository, times(0)).save(master)
    }

    @Test
    @Transactional
    fun deleteMaster() {
        // Initialize the database
        masterService.save(master)

        val databaseSizeBeforeDelete = masterRepository.findAll().size

        val id = master.id
        assertNotNull(id)

        // Delete the master
        restMasterMockMvc.perform(
            delete("/api/masters/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val masterList = masterRepository.findAll()
        assertThat(masterList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Master in Elasticsearch
        verify(mockMasterSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchMaster() {
        // Initialize the database
        masterService.save(master)
        `when`(mockMasterSearchRepository.search(queryStringQuery("id:" + master.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(master), PageRequest.of(0, 1), 1))
        // Search the master
        restMasterMockMvc.perform(get("/api/_search/masters?query=id:" + master.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(master.id?.toInt())))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(Master::class.java)
        val master1 = Master()
        master1.id = 1L
        val master2 = Master()
        master2.id = master1.id
        assertThat(master1).isEqualTo(master2)
        master2.id = 2L
        assertThat(master1).isNotEqualTo(master2)
        master1.id = null
        assertThat(master1).isNotEqualTo(master2)
    }

    companion object {
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Master {
            val master = Master()

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            master.user = user
            return master
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Master {
            val master = Master()

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            master.user = user
            return master
        }
    }
}
