package com.beautypoint.app.service.dto

import java.io.Serializable
import java.util.*

/**
 * A DTO for the [com.beautypoint.app.domain.Master] entity.
 */
data class MasterDTO(

    var id: Long? = null,

    var salonId: Long? = null,

    var categories: MutableSet<CategoryDTO> = mutableSetOf(),

    var userId: Long? = null,

    var userLogin: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MasterDTO) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
