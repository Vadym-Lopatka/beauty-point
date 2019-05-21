package com.beautypoint.app.service

import com.beautypoint.app.domain.Master_
import com.beautypoint.app.domain.Offer_
import com.beautypoint.app.domain.Variant
import com.beautypoint.app.domain.Variant_
import com.beautypoint.app.repository.VariantRepository
import com.beautypoint.app.repository.search.VariantSearchRepository
import com.beautypoint.app.service.dto.VariantCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [Variant] entities in the database.
 * The main input is a [VariantCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Variant] or a [Page] of [Variant] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class VariantQueryService(
    val variantRepository: VariantRepository,
    val variantSearchRepository: VariantSearchRepository
) : QueryService<Variant>() {

    private val log = LoggerFactory.getLogger(VariantQueryService::class.java)

    /**
     * Return a [MutableList] of [Variant] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: VariantCriteria?): MutableList<Variant> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return variantRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Variant] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: VariantCriteria?, page: Pageable): Page<Variant> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return variantRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: VariantCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return variantRepository.count(specification)
    }

    /**
     * Function to convert [VariantCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: VariantCriteria?): Specification<Variant?> {
        var specification: Specification<Variant?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, Variant_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Variant_.name))
            }
            if (criteria.price != null) {
                specification = specification.and(buildRangeSpecification(criteria.price, Variant_.price))
            }
            if (criteria.sessionTime != null) {
                specification = specification.and(buildRangeSpecification(criteria.sessionTime, Variant_.sessionTime))
            }
            if (criteria.active != null) {
                specification = specification.and(buildSpecification(criteria.active, Variant_.active))
            }
            if (criteria.offerId != null) {
                specification = specification.and(buildSpecification(criteria.offerId) {
                    it.join(Variant_.offer, JoinType.LEFT).get(Offer_.id)
                })
            }
            if (criteria.executorId != null) {
                specification = specification.and(buildSpecification(criteria.executorId) {
                    it.join(Variant_.executors, JoinType.LEFT).get(Master_.id)
                })
            }
        }
        return specification
    }
}
