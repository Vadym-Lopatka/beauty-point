package com.beautypoint.app.service.dto

import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.BooleanFilter
import io.github.jhipster.service.filter.LongFilter
import java.io.Serializable

/**
 * Criteria class for the [com.beautypoint.app.domain.TimeTable] entity. This class is used in
 * [com.beautypoint.app.web.rest.TimeTableResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/time-tables?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class TimeTableCriteria(

    var id: LongFilter? = null,

    var everyDayEqual: BooleanFilter? = null,

    var mo: LongFilter? = null,

    var tu: LongFilter? = null,

    var we: LongFilter? = null,

    var th: LongFilter? = null,

    var fr: LongFilter? = null,

    var sa: LongFilter? = null,

    var su: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: TimeTableCriteria) :
        this(
            other.id?.copy(),
            other.everyDayEqual?.copy(),
            other.mo?.copy(),
            other.tu?.copy(),
            other.we?.copy(),
            other.th?.copy(),
            other.fr?.copy(),
            other.sa?.copy(),
            other.su?.copy()
        )

    override fun copy(): TimeTableCriteria {
        return TimeTableCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
