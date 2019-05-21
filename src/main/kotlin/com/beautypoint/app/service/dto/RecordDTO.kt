package com.beautypoint.app.service.dto

import com.beautypoint.app.domain.enumeration.OrderStatusEnum
import java.io.Serializable
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.beautypoint.app.domain.Record] entity.
 */
data class RecordDTO(

    var id: Long? = null,

    @get: NotNull
    var bookingTime: Instant? = null,

    @get: NotNull
    var duration: Int? = null,

    @get: NotNull
    var totalPrice: BigDecimal? = null,

    @get: NotNull
    var orderStatus: OrderStatusEnum? = null,

    var comment: String? = null,

    var masterId: Long? = null,

    var variantId: Long? = null,

    var options: MutableSet<OptionDTO> = mutableSetOf(),

    var userId: Long? = null,

    var userLogin: String? = null,

    var salonId: Long? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecordDTO) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
