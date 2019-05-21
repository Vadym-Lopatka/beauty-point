package com.beautypoint.app.service.dto

import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.LongFilter
import java.io.Serializable

/**
 * Criteria class for the [com.beautypoint.app.domain.Master] entity. This class is used in
 * [com.beautypoint.app.web.rest.MasterResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/masters?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class MasterCriteria(

    var id: LongFilter? = null,

    var salonId: LongFilter? = null,

    var recordId: LongFilter? = null,

    var categoryId: LongFilter? = null,

    var userId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: MasterCriteria) :
        this(
            other.id?.copy(),
            other.salonId?.copy(),
            other.recordId?.copy(),
            other.categoryId?.copy(),
            other.userId?.copy()
        )

    override fun copy(): MasterCriteria {
        return MasterCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
