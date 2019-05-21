package com.beautypoint.app.service.dto

import java.io.Serializable
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.beautypoint.app.domain.Category] entity.
 */
data class CategoryDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    @get: NotNull
    var isMain: Boolean? = null,

    var parentId: Long? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CategoryDTO) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
