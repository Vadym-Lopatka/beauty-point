package com.beautypoint.app.service.dto

import com.beautypoint.app.domain.enumeration.OfferStatusEnum
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.*
import java.io.Serializable

/**
 * Criteria class for the [com.beautypoint.app.domain.Offer] entity. This class is used in
 * [com.beautypoint.app.web.rest.OfferResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/offers?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class OfferCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var description: StringFilter? = null,

    var priceLow: BigDecimalFilter? = null,

    var priceHigh: BigDecimalFilter? = null,

    var active: BooleanFilter? = null,

    var status: OfferStatusEnumFilter? = null,

    var salonId: LongFilter? = null,

    var imageId: LongFilter? = null,

    var variantId: LongFilter? = null,

    var optionId: LongFilter? = null,

    var categoryId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: OfferCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.description?.copy(),
            other.priceLow?.copy(),
            other.priceHigh?.copy(),
            other.active?.copy(),
            other.status?.copy(),
            other.salonId?.copy(),
            other.imageId?.copy(),
            other.variantId?.copy(),
            other.optionId?.copy(),
            other.categoryId?.copy()
        )

    /**
     * Class for filtering OfferStatusEnum
     */
    class OfferStatusEnumFilter : Filter<OfferStatusEnum> {
        constructor()

        constructor(filter: OfferStatusEnumFilter) : super(filter)

        override fun copy(): OfferStatusEnumFilter {
            return OfferStatusEnumFilter(this)
        }
    }

    override fun copy(): OfferCriteria {
        return OfferCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
