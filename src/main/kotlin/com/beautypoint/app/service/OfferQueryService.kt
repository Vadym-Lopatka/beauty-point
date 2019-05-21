package com.beautypoint.app.service

import com.beautypoint.app.domain.*
import com.beautypoint.app.repository.OfferRepository
import com.beautypoint.app.repository.search.OfferSearchRepository
import com.beautypoint.app.service.dto.OfferCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [Offer] entities in the database.
 * The main input is a [OfferCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Offer] or a [Page] of [Offer] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class OfferQueryService(
    val offerRepository: OfferRepository,
    val offerSearchRepository: OfferSearchRepository
) : QueryService<Offer>() {

    private val log = LoggerFactory.getLogger(OfferQueryService::class.java)

    /**
     * Return a [MutableList] of [Offer] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: OfferCriteria?): MutableList<Offer> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return offerRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Offer] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: OfferCriteria?, page: Pageable): Page<Offer> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return offerRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: OfferCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return offerRepository.count(specification)
    }

    /**
     * Function to convert [OfferCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: OfferCriteria?): Specification<Offer?> {
        var specification: Specification<Offer?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, Offer_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Offer_.name))
            }
            if (criteria.description != null) {
                specification = specification.and(buildStringSpecification(criteria.description, Offer_.description))
            }
            if (criteria.priceLow != null) {
                specification = specification.and(buildRangeSpecification(criteria.priceLow, Offer_.priceLow))
            }
            if (criteria.priceHigh != null) {
                specification = specification.and(buildRangeSpecification(criteria.priceHigh, Offer_.priceHigh))
            }
            if (criteria.active != null) {
                specification = specification.and(buildSpecification(criteria.active, Offer_.active))
            }
            if (criteria.status != null) {
                specification = specification.and(buildSpecification(criteria.status, Offer_.status))
            }
            if (criteria.salonId != null) {
                specification = specification.and(buildSpecification(criteria.salonId) {
                    it.join(Offer_.salon, JoinType.LEFT).get(Salon_.id)
                })
            }
            if (criteria.imageId != null) {
                specification = specification.and(buildSpecification(criteria.imageId) {
                    it.join(Offer_.image, JoinType.LEFT).get(Image_.id)
                })
            }
            if (criteria.variantId != null) {
                specification = specification.and(buildSpecification(criteria.variantId) {
                    it.join(Offer_.variants, JoinType.LEFT).get(Variant_.id)
                })
            }
            if (criteria.optionId != null) {
                specification = specification.and(buildSpecification(criteria.optionId) {
                    it.join(Offer_.options, JoinType.LEFT).get(Option_.id)
                })
            }
            if (criteria.categoryId != null) {
                specification = specification.and(buildSpecification(criteria.categoryId) {
                    it.join(Offer_.category, JoinType.LEFT).get(Category_.id)
                })
            }
        }
        return specification
    }
}
