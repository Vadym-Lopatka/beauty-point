package com.beautypoint.app.service.dto

import com.beautypoint.app.domain.enumeration.SalonStatusEnum
import com.beautypoint.app.domain.enumeration.SalonTypeEnum
import java.io.Serializable
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.beautypoint.app.domain.Salon] entity.
 */
data class SalonDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    var slogan: String? = null,

    @get: NotNull
    var location: String? = null,

    @get: NotNull
    var status: SalonStatusEnum? = null,

    var systemComment: String? = null,

    @get: NotNull
    var type: SalonTypeEnum? = null,

    var imageId: Long? = null,

    var timeTableId: Long? = null,

    var categories: MutableSet<CategoryDTO> = mutableSetOf(),

    var ownerId: Long? = null,

    var ownerLogin: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SalonDTO) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
