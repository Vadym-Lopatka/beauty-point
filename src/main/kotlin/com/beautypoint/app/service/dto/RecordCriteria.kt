package com.beautypoint.app.service.dto

import com.beautypoint.app.domain.enumeration.OrderStatusEnum
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.*
import java.io.Serializable

/**
 * Criteria class for the [com.beautypoint.app.domain.Record] entity. This class is used in
 * [com.beautypoint.app.web.rest.RecordResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/records?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class RecordCriteria(

    var id: LongFilter? = null,

    var bookingTime: InstantFilter? = null,

    var duration: IntegerFilter? = null,

    var totalPrice: BigDecimalFilter? = null,

    var orderStatus: OrderStatusEnumFilter? = null,

    var comment: StringFilter? = null,

    var masterId: LongFilter? = null,

    var variantId: LongFilter? = null,

    var optionId: LongFilter? = null,

    var userId: LongFilter? = null,

    var salonId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: RecordCriteria) :
        this(
            other.id?.copy(),
            other.bookingTime?.copy(),
            other.duration?.copy(),
            other.totalPrice?.copy(),
            other.orderStatus?.copy(),
            other.comment?.copy(),
            other.masterId?.copy(),
            other.variantId?.copy(),
            other.optionId?.copy(),
            other.userId?.copy(),
            other.salonId?.copy()
        )

    /**
     * Class for filtering OrderStatusEnum
     */
    class OrderStatusEnumFilter : Filter<OrderStatusEnum> {
        constructor()

        constructor(filter: OrderStatusEnumFilter) : super(filter)

        override fun copy(): OrderStatusEnumFilter {
            return OrderStatusEnumFilter(this)
        }
    }

    override fun copy(): RecordCriteria {
        return RecordCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
