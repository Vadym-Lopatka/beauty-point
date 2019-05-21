package com.beautypoint.app.service.dto

import io.github.jhipster.service.Criteria
import io.github.jhipster.service.filter.BooleanFilter
import io.github.jhipster.service.filter.LongFilter
import io.github.jhipster.service.filter.StringFilter
import java.io.Serializable

/**
 * Criteria class for the [com.beautypoint.app.domain.Category] entity. This class is used in
 * [com.beautypoint.app.web.rest.CategoryResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
data class CategoryCriteria(

    var id: LongFilter? = null,

    var name: StringFilter? = null,

    var main: BooleanFilter? = null,

    var parentId: LongFilter? = null,

    var salonId: LongFilter? = null,

    var masterId: LongFilter? = null
) : Serializable, Criteria {

    constructor(other: CategoryCriteria) :
        this(
            other.id?.copy(),
            other.name?.copy(),
            other.main?.copy(),
            other.parentId?.copy(),
            other.salonId?.copy(),
            other.masterId?.copy()
        )

    override fun copy(): CategoryCriteria {
        return CategoryCriteria(this)
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
