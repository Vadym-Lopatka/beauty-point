package com.beautypoint.app.repository

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.config.Constants
import com.beautypoint.app.config.audit.AuditEventConverter
import com.beautypoint.app.domain.PersistentAuditEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Integration tests for [CustomAuditEventRepository].
 */
@SpringBootTest(classes = [BeautyPointApp::class])
@Transactional
class CustomAuditEventRepositoryIT {

    @Autowired
    private lateinit var persistenceAuditEventRepository: PersistenceAuditEventRepository

    @Autowired
    private lateinit var auditEventConverter: AuditEventConverter

    private lateinit var customAuditEventRepository: CustomAuditEventRepository

    private lateinit var testUserEvent: PersistentAuditEvent

    private lateinit var testOtherUserEvent: PersistentAuditEvent

    private lateinit var testOldUserEvent: PersistentAuditEvent

    @BeforeEach
    fun setup() {
        customAuditEventRepository = CustomAuditEventRepository(persistenceAuditEventRepository, auditEventConverter)
        persistenceAuditEventRepository.deleteAll()
        val oneHourAgo = Instant.now().minusSeconds(3600)

        testUserEvent = PersistentAuditEvent()
        testUserEvent.principal = "test-user"
        testUserEvent.auditEventType = "test-type"
        testUserEvent.auditEventDate = oneHourAgo
        val data = mutableMapOf<String, String?>()
        data["test-key"] = "test-value"
        testUserEvent.data = data

        testOldUserEvent = PersistentAuditEvent()
        testOldUserEvent.principal = "test-user"
        testOldUserEvent.auditEventType = "test-type"
        testOldUserEvent.auditEventDate = oneHourAgo.minusSeconds(10000)

        testOtherUserEvent = PersistentAuditEvent()
        testOtherUserEvent.principal = "other-test-user"
        testOtherUserEvent.auditEventType = "test-type"
        testOtherUserEvent.auditEventDate = oneHourAgo
    }

    @Test
    fun addAuditEvent() {
        val data = mutableMapOf<String, Any>()
        data["test-key"] = "test-value"
        val event = AuditEvent("test-user", "test-type", data)
        customAuditEventRepository.add(event)
        val persistentAuditEvents = persistenceAuditEventRepository.findAll()
        assertThat(persistentAuditEvents).hasSize(1)
        val persistentAuditEvent = persistentAuditEvents[0]
        assertThat(persistentAuditEvent.principal).isEqualTo(event.principal)
        assertThat(persistentAuditEvent.auditEventType).isEqualTo(event.type)
        assertThat(persistentAuditEvent.data).containsKey("test-key")
        assertThat(persistentAuditEvent.data["test-key"]).isEqualTo("test-value")
        assertThat(persistentAuditEvent.auditEventDate!!.truncatedTo(ChronoUnit.MILLIS))
            .isEqualTo(event.timestamp!!.truncatedTo(ChronoUnit.MILLIS))
    }

    @Test
    fun addAuditEventTruncateLargeData() {
        val data = mutableMapOf<String, Any>()
        val largeData = StringBuilder()
        for (i in 0 until CustomAuditEventRepository.EVENT_DATA_COLUMN_MAX_LENGTH + 10) {
            largeData.append("a")
        }
        data["test-key"] = largeData
        val event = AuditEvent("test-user", "test-type", data)
        customAuditEventRepository.add(event)
        val persistentAuditEvents = persistenceAuditEventRepository.findAll()
        assertThat(persistentAuditEvents).hasSize(1)
        val persistentAuditEvent = persistentAuditEvents[0]
        assertThat(persistentAuditEvent.principal).isEqualTo(event.principal)
        assertThat(persistentAuditEvent.auditEventType).isEqualTo(event.type)
        assertThat(persistentAuditEvent.data).containsKey("test-key")
        val actualData = persistentAuditEvent.data["test-key"]
        assertThat(actualData!!.length).isEqualTo(CustomAuditEventRepository.EVENT_DATA_COLUMN_MAX_LENGTH)
        assertThat(actualData).isSubstringOf(largeData)
        assertThat(persistentAuditEvent.auditEventDate!!.truncatedTo(ChronoUnit.MILLIS))
            .isEqualTo(event.timestamp!!.truncatedTo(ChronoUnit.MILLIS))
    }

    @Test
    fun testAddEventWithWebAuthenticationDetails() {
        val session = MockHttpSession(null, "test-session-id")
        val request = MockHttpServletRequest()
        request.session = session
        request.remoteAddr = "1.2.3.4"
        val details = WebAuthenticationDetails(request)
        val data = mutableMapOf<String, Any>()
        data["test-key"] = details
        val event = AuditEvent("test-user", "test-type", data)
        customAuditEventRepository.add(event)
        val persistentAuditEvents = persistenceAuditEventRepository.findAll()
        assertThat(persistentAuditEvents).hasSize(1)
        val persistentAuditEvent = persistentAuditEvents[0]
        assertThat(persistentAuditEvent.data["remoteAddress"]).isEqualTo("1.2.3.4")
        assertThat(persistentAuditEvent.data["sessionId"]).isEqualTo("test-session-id")
    }

    @Test
    fun testAddEventWithNullData() {
        val data = mutableMapOf<String, Any?>()
        data["test-key"] = null
        val event = AuditEvent("test-user", "test-type", data)
        customAuditEventRepository.add(event)
        val persistentAuditEvents = persistenceAuditEventRepository.findAll()
        assertThat(persistentAuditEvents).hasSize(1)
        val persistentAuditEvent = persistentAuditEvents[0]
        assertThat(persistentAuditEvent.data["test-key"]).isNull()
    }

    @Test
    fun addAuditEventWithAnonymousUser() {
        val data = mutableMapOf<String, Any>()
        data["test-key"] = "test-value"
        val event = AuditEvent(Constants.ANONYMOUS_USER, "test-type", data)
        customAuditEventRepository.add(event)
        val persistentAuditEvents = persistenceAuditEventRepository.findAll()
        assertThat(persistentAuditEvents).hasSize(0)
    }

    @Test
    fun addAuditEventWithAuthorizationFailureType() {
        val data = mutableMapOf<String, Any>()
        data["test-key"] = "test-value"
        val event = AuditEvent("test-user", "AUTHORIZATION_FAILURE", data)
        customAuditEventRepository.add(event)
        val persistentAuditEvents = persistenceAuditEventRepository.findAll()
        assertThat(persistentAuditEvents).hasSize(0)
    }
}
