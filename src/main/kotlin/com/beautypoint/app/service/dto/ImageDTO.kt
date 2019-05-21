package com.beautypoint.app.service.dto

import com.beautypoint.app.domain.enumeration.ImageTypeEnum
import java.io.Serializable
import java.util.*
import javax.validation.constraints.NotNull

/**
 * A DTO for the [com.beautypoint.app.domain.Image] entity.
 */
data class ImageDTO(

    var id: Long? = null,

    @get: NotNull
    var source: String? = null,

    @get: NotNull
    var imageType: ImageTypeEnum? = null,

    var ownerId: Long? = null,

    var ownerLogin: String? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageDTO) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
