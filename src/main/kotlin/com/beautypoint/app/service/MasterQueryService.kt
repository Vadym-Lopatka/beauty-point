package com.beautypoint.app.service

import com.beautypoint.app.domain.*
import com.beautypoint.app.repository.MasterRepository
import com.beautypoint.app.repository.search.MasterSearchRepository
import com.beautypoint.app.service.dto.MasterCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [Master] entities in the database.
 * The main input is a [MasterCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Master] or a [Page] of [Master] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class MasterQueryService(
    val masterRepository: MasterRepository,
    val masterSearchRepository: MasterSearchRepository
) : QueryService<Master>() {

    private val log = LoggerFactory.getLogger(MasterQueryService::class.java)

    /**
     * Return a [MutableList] of [Master] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MasterCriteria?): MutableList<Master> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return masterRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Master] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MasterCriteria?, page: Pageable): Page<Master> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return masterRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: MasterCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return masterRepository.count(specification)
    }

    /**
     * Function to convert [MasterCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: MasterCriteria?): Specification<Master?> {
        var specification: Specification<Master?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, Master_.id))
            }
            if (criteria.salonId != null) {
                specification = specification.and(buildSpecification(criteria.salonId) {
                    it.join(Master_.salon, JoinType.LEFT).get(Salon_.id)
                })
            }
            if (criteria.recordId != null) {
                specification = specification.and(buildSpecification(criteria.recordId) {
                    it.join(Master_.records, JoinType.LEFT).get(Record_.id)
                })
            }
            if (criteria.categoryId != null) {
                specification = specification.and(buildSpecification(criteria.categoryId) {
                    it.join(Master_.categories, JoinType.LEFT).get(Category_.id)
                })
            }
            if (criteria.userId != null) {
                specification = specification.and(buildSpecification(criteria.userId) {
                    it.join(Master_.user, JoinType.LEFT).get(User_.id)
                })
            }
        }
        return specification
    }
}
