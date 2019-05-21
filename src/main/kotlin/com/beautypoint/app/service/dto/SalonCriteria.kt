package com.beautypoint.app.service.dto

import com.beautypoint.app.domain.enumeration.SalonStatusEnum
import com.beautypoint.app.domain.enumeration.SalonTypeEnum
import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.Filter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [com.beautypoint.app.domain.Salon] entity. This class is used in
 * [com.beautypoint.app.web.rest.SalonResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/salons?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class SalonCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var slogan: StringFilter? = null,

    var location: StringFilter? = null,

    var status: SalonStatusEnumFilter? = null,

    var systemComment: StringFilter? = null,

    var type: SalonTypeEnumFilter? = null,

    var imageId: LongFilter? = null,

    var timeTableId: LongFilter? = null,

    var offerId: LongFilter? = null,

    var masterId: LongFilter? = null,

    var categoryId: LongFilter? = null,

    var ownerId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: SalonCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.slogan?.copy(),
            other.location?.copy(),
            other.status?.copy(),
            other.systemComment?.copy(),
            other.type?.copy(),
            other.imageId?.copy(),
            other.timeTableId?.copy(),
            other.offerId?.copy(),
            other.masterId?.copy(),
            other.categoryId?.copy(),
            other.ownerId?.copy()
        )

    /**
     * Class for filtering SalonStatusEnum
     */
    class SalonStatusEnumFilter : Filter<SalonStatusEnum> {
        constructor()

        constructor(filter: SalonStatusEnumFilter) : super(filter)

        override fun copy(): SalonStatusEnumFilter {
            return SalonStatusEnumFilter(this)
        }
    }

    /**
     * Class for filtering SalonTypeEnum
     */
    class SalonTypeEnumFilter : Filter<SalonTypeEnum> {
        constructor()

        constructor(filter: SalonTypeEnumFilter) : super(filter)

        override fun copy(): SalonTypeEnumFilter {
            return SalonTypeEnumFilter(this)
        }
    }

    override fun copy(): SalonCriteria {
        return SalonCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
