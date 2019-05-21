package com.beautypoint.app.service.dto

import com.beautypoint.app.domain.enumeration.OfferStatusEnum
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.beautypoint.app.domain.Offer] entity.
 */
data class OfferDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    @get: NotNull
    var description: String? = null,

    @get: NotNull
    var priceLow: BigDecimal? = null,

    @get: NotNull
    var priceHigh: BigDecimal? = null,

    @get: NotNull
    var isActive: Boolean? = null,

    @get: NotNull
    var status: OfferStatusEnum? = null,

    var salonId: Long? = null,

    var imageId: Long? = null,

    var categoryId: Long? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OfferDTO) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
