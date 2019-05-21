package com.beautypoint.app.service.dto

import com.beautypoint.app.domain.enumeration.ImageTypeEnum
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.Filter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [com.beautypoint.app.domain.Image] entity. This class is used in
 * [com.beautypoint.app.web.rest.ImageResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/images?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class ImageCriteria(

    var id: LongFilter? = null,

    var source: StringFilter? = null,

    var imageType: ImageTypeEnumFilter? = null,

    var ownerId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: ImageCriteria) :
        this(
            other.id?.copy(),
            other.source?.copy(),
            other.imageType?.copy(),
            other.ownerId?.copy()
        )

    /**
     * Class for filtering ImageTypeEnum
     */
    class ImageTypeEnumFilter : Filter<ImageTypeEnum> {
        constructor()

        constructor(filter: ImageTypeEnumFilter) : super(filter)

        override fun copy(): ImageTypeEnumFilter {
            return ImageTypeEnumFilter(this)
        }
    }

    override fun copy(): ImageCriteria {
        return ImageCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
