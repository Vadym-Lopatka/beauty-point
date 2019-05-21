package com.beautypoint.app.service.dto

import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [com.beautypoint.app.domain.Subscriber] entity. This class is used in
 * [com.beautypoint.app.web.rest.SubscriberResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/subscribers?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class SubscriberCriteria(

    var id: LongFilter? = null,

    var firsName: StringFilter? = null,

    var email: StringFilter? = null
) : Serializable, Criteria {

    constructor(other: SubscriberCriteria) :
        this(
            other.id?.copy(),
            other.firsName?.copy(),
            other.email?.copy()
        )

    override fun copy(): SubscriberCriteria {
        return SubscriberCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
