package com.beautypoint.app.service

import com.beautypoint.app.domain.Offer_
import com.beautypoint.app.domain.Option
import com.beautypoint.app.domain.Option_
import com.beautypoint.app.repository.OptionRepository
import com.beautypoint.app.repository.search.OptionSearchRepository
import com.beautypoint.app.service.dto.OptionCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [Option] entities in the database.
 * The main input is a [OptionCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Option] or a [Page] of [Option] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class OptionQueryService(
    val optionRepository: OptionRepository,
    val optionSearchRepository: OptionSearchRepository
) : QueryService<Option>() {

    private val log = LoggerFactory.getLogger(OptionQueryService::class.java)

    /**
     * Return a [MutableList] of [Option] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: OptionCriteria?): MutableList<Option> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return optionRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Option] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: OptionCriteria?, page: Pageable): Page<Option> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return optionRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: OptionCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return optionRepository.count(specification)
    }

    /**
     * Function to convert [OptionCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: OptionCriteria?): Specification<Option?> {
        var specification: Specification<Option?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, Option_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Option_.name))
            }
            if (criteria.price != null) {
                specification = specification.and(buildRangeSpecification(criteria.price, Option_.price))
            }
            if (criteria.sessionTime != null) {
                specification = specification.and(buildRangeSpecification(criteria.sessionTime, Option_.sessionTime))
            }
            if (criteria.active != null) {
                specification = specification.and(buildSpecification(criteria.active, Option_.active))
            }
            if (criteria.offerId != null) {
                specification = specification.and(buildSpecification(criteria.offerId) {
                    it.join(Option_.offer, JoinType.LEFT).get(Offer_.id)
                })
            }
        }
        return specification
    }
}
