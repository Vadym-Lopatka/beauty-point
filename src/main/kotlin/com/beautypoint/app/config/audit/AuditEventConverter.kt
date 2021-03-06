package com.beautypoint.app.config.audit

import com.beautypoint.app.domain.PersistentAuditEvent

import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component

@Component
class AuditEventConverter {

    /**
     * Convert a list of [PersistentAuditEvent] to a list of [AuditEvent].
     *
     * @param persistentAuditEvents the list to convert.
     * @return the converted list.
     */
    fun convertToAuditEvent(persistentAuditEvents: Iterable<PersistentAuditEvent>?): MutableList<AuditEvent> {
        if (persistentAuditEvents == null) {
            return mutableListOf()
        }
        return persistentAuditEvents.asSequence().mapNotNull { convertToAuditEvent(it) }.toMutableList()
    }

    /**
     * Convert a [PersistentAuditEvent] to an [AuditEvent].
     *
     * @param persistentAuditEvent the event to convert.
     * @return the converted list.
     */
    fun convertToAuditEvent(persistentAuditEvent: PersistentAuditEvent?): AuditEvent? {
        return when (persistentAuditEvent) {
            null -> null
            else -> AuditEvent(persistentAuditEvent.auditEventDate, persistentAuditEvent.principal,
                persistentAuditEvent.auditEventType, convertDataToObjects(persistentAuditEvent.data))
        }
    }

    /**
     * Internal conversion. This is needed to support the current SpringBoot actuator[ AuditEventRepository] interface.
     *
     * @param data the data to convert.
     * @return a map of [String], [Any].
     */
    fun convertDataToObjects(data: MutableMap<String, String?>?): MutableMap<String, Any?> {
        val results = mutableMapOf<String, Any?>()
        if (data != null) {
            for ((key, value) in data) {
                results[key] = value
            }
        }
        return results
    }

    /**
     * Internal conversion. This method will allow to save additional data.
     * By default, it will save the object as string.
     *
     * @param data the data to convert.
     * @return a map of [String], [String].
     */
    fun convertDataToStrings(data: Map<String, Any?>?): Map<String, String?> {
        val results = mutableMapOf<String, String?>()

        if (data != null) {
            for ((key, value) in data) {
                // Extract the data that will be saved.
                if (value is WebAuthenticationDetails) {
                    results["remoteAddress"] = value.remoteAddress
                    results["sessionId"] = value.sessionId
                } else {
                    results[key] = value?.toString()
                }
            }
        }
        return results
    }
}
