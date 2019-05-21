package com.beautypoint.app.service.dto

import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.*
import java.io.Serializable

/**
 * Criteria class for the [com.beautypoint.app.domain.Variant] entity. This class is used in
 * [com.beautypoint.app.web.rest.VariantResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/variants?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class VariantCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var price: BigDecimalFilter? = null,

    var sessionTime: IntegerFilter? = null,

    var active: BooleanFilter? = null,

    var offerId: LongFilter? = null,

    var executorId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: VariantCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.price?.copy(),
            other.sessionTime?.copy(),
            other.active?.copy(),
            other.offerId?.copy(),
            other.executorId?.copy()
        )

    override fun copy(): VariantCriteria {
        return VariantCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
