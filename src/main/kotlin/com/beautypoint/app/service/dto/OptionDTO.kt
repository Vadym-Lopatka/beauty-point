package com.beautypoint.app.service.dto

import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.beautypoint.app.domain.Option] entity.
 */
data class OptionDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    @get: NotNull
    var price: BigDecimal? = null,

    @get: NotNull
    var sessionTime: Int? = null,

    @get: NotNull
    var isActive: Boolean? = null,

    var offerId: Long? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OptionDTO) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
