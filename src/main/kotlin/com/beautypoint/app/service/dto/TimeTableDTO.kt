package com.beautypoint.app.service.dto

import java.io.Serializable
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.beautypoint.app.domain.TimeTable] entity.
 */
data class TimeTableDTO(

    var id: Long? = null,

    @get: NotNull
    var isEveryDayEqual: Boolean? = null,

    @get: NotNull
    var mo: Long? = null,

    @get: NotNull
    var tu: Long? = null,

    @get: NotNull
    var we: Long? = null,

    @get: NotNull
    var th: Long? = null,

    @get: NotNull
    var fr: Long? = null,

    @get: NotNull
    var sa: Long? = null,

    @get: NotNull
    var su: Long? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeTableDTO) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
