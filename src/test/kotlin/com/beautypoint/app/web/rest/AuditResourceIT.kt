package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.config.audit.AuditEventConverter
import com.beautypoint.app.domain.PersistentAuditEvent
import com.beautypoint.app.repository.PersistenceAuditEventRepository
import com.beautypoint.app.service.AuditEventService
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.format.support.FormattingConversionService
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Integration tests for the [AuditResource] REST controller.
 */
@SpringBootTest(classes = [BeautyPointApp::class])
@Transactional
class AuditResourceIT {

    @Autowired
    private lateinit var auditEventRepository: PersistenceAuditEventRepository

    @Autowired
    private lateinit var auditEventConverter: AuditEventConverter

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    @Qualifier("mvcConversionService")
    private lateinit var formattingConversionService: FormattingConversionService

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    private lateinit var auditEvent: PersistentAuditEvent

    private lateinit var restAuditMockMvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val auditEventService = AuditEventService(auditEventRepository, auditEventConverter)
        val auditResource = AuditResource(auditEventService)
        this.restAuditMockMvc = MockMvcBuilders.standaloneSetup(auditResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setConversionService(formattingConversionService)
            .setMessageConverters(jacksonMessageConverter).build()
    }

    @BeforeEach
    fun initTest() {
        auditEventRepository.deleteAll()
        auditEvent = PersistentAuditEvent()
        auditEvent.auditEventType = SAMPLE_TYPE
        auditEvent.principal = SAMPLE_PRINCIPAL
        auditEvent.auditEventDate = SAMPLE_TIMESTAMP
    }

    @Test
    @Throws(Exception::class)
    fun getAllAudits() {
        // Initialize the database
        auditEventRepository.save(auditEvent)

        // Get all the audits
        restAuditMockMvc.perform(get("/management/audits"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("\$.[*].principal").value(hasItem(SAMPLE_PRINCIPAL)))
    }

    @Test
    @Throws(Exception::class)
    fun getAudit() {
        // Initialize the database
        auditEventRepository.save(auditEvent)

        // Get the audit
        restAuditMockMvc.perform(get("/management/audits/{id}", auditEvent.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("\$.principal").value(SAMPLE_PRINCIPAL))
    }

    @Test
    @Throws(Exception::class)
    fun getAuditsByDate() {
        // Initialize the database
        auditEventRepository.save(auditEvent)

        // Generate dates for selecting audits by date, making sure the period will contain the audit
        val fromDate = SAMPLE_TIMESTAMP.minusSeconds(SECONDS_PER_DAY).toString().substring(0, 10)
        val toDate = SAMPLE_TIMESTAMP.plusSeconds(SECONDS_PER_DAY).toString().substring(0, 10)

        // Get the audit
        restAuditMockMvc.perform(get("/management/audits?fromDate=$fromDate&toDate=$toDate"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("\$.[*].principal").value(hasItem(SAMPLE_PRINCIPAL)))
    }

    @Test
    @Throws(Exception::class)
    fun getNonExistingAuditsByDate() {
        // Initialize the database
        auditEventRepository.save(auditEvent)

        // Generate dates for selecting audits by date, making sure the period will not contain the sample audit
        val fromDate = SAMPLE_TIMESTAMP.minusSeconds(2 * SECONDS_PER_DAY).toString().substring(0, 10)
        val toDate = SAMPLE_TIMESTAMP.minusSeconds(SECONDS_PER_DAY).toString().substring(0, 10)

        // Query audits but expect no results
        restAuditMockMvc.perform(get("/management/audits?fromDate=$fromDate&toDate=$toDate"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(header().string("X-Total-Count", "0"))
    }

    @Test
    @Throws(Exception::class)
    fun getNonExistingAudit() {
        // Get the audit
        restAuditMockMvc.perform(get("/management/audits/{id}", java.lang.Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun testPersistentAuditEventEquals() {
        TestUtil.equalsVerifier(PersistentAuditEvent::class.java)
        val auditEvent1 = PersistentAuditEvent()
        auditEvent1.id = 1L
        val auditEvent2 = PersistentAuditEvent()
        auditEvent2.id = auditEvent1.id
        assertThat(auditEvent1).isEqualTo(auditEvent2)
        auditEvent2.id = 2L
        assertThat(auditEvent1).isNotEqualTo(auditEvent2)
        auditEvent1.id = null
        assertThat(auditEvent1).isNotEqualTo(auditEvent2)
    }

    companion object {

        private const val SAMPLE_PRINCIPAL = "SAMPLE_PRINCIPAL"
        private const val SAMPLE_TYPE = "SAMPLE_TYPE"
        private val SAMPLE_TIMESTAMP = Instant.parse("2015-08-04T10:11:30Z")
        private const val SECONDS_PER_DAY = (60 * 60 * 24).toLong()
    }
}
