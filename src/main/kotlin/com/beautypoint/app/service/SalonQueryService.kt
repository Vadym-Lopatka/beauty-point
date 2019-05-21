package com.beautypoint.app.service

import com.beautypoint.app.domain.*
import com.beautypoint.app.repository.SalonRepository
import com.beautypoint.app.repository.search.SalonSearchRepository
import com.beautypoint.app.service.dto.SalonCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [Salon] entities in the database.
 * The main input is a [SalonCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Salon] or a [Page] of [Salon] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class SalonQueryService(
    val salonRepository: SalonRepository,
    val salonSearchRepository: SalonSearchRepository
) : QueryService<Salon>() {

    private val log = LoggerFactory.getLogger(SalonQueryService::class.java)

    /**
     * Return a [MutableList] of [Salon] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: SalonCriteria?): MutableList<Salon> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return salonRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Salon] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: SalonCriteria?, page: Pageable): Page<Salon> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return salonRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: SalonCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return salonRepository.count(specification)
    }

    /**
     * Function to convert [SalonCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: SalonCriteria?): Specification<Salon?> {
        var specification: Specification<Salon?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, Salon_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Salon_.name))
            }
            if (criteria.slogan != null) {
                specification = specification.and(buildStringSpecification(criteria.slogan, Salon_.slogan))
            }
            if (criteria.location != null) {
                specification = specification.and(buildStringSpecification(criteria.location, Salon_.location))
            }
            if (criteria.status != null) {
                specification = specification.and(buildSpecification(criteria.status, Salon_.status))
            }
            if (criteria.systemComment != null) {
                specification = specification.and(buildStringSpecification(criteria.systemComment, Salon_.systemComment))
            }
            if (criteria.type != null) {
                specification = specification.and(buildSpecification(criteria.type, Salon_.type))
            }
            if (criteria.imageId != null) {
                specification = specification.and(buildSpecification(criteria.imageId) {
                    it.join(Salon_.image, JoinType.LEFT).get(Image_.id)
                })
            }
            if (criteria.timeTableId != null) {
                specification = specification.and(buildSpecification(criteria.timeTableId) {
                    it.join(Salon_.timeTable, JoinType.LEFT).get(TimeTable_.id)
                })
            }
            if (criteria.offerId != null) {
                specification = specification.and(buildSpecification(criteria.offerId) {
                    it.join(Salon_.offers, JoinType.LEFT).get(Offer_.id)
                })
            }
            if (criteria.masterId != null) {
                specification = specification.and(buildSpecification(criteria.masterId) {
                    it.join(Salon_.masters, JoinType.LEFT).get(Master_.id)
                })
            }
            if (criteria.categoryId != null) {
                specification = specification.and(buildSpecification(criteria.categoryId) {
                    it.join(Salon_.categories, JoinType.LEFT).get(Category_.id)
                })
            }
            if (criteria.ownerId != null) {
                specification = specification.and(buildSpecification(criteria.ownerId) {
                    it.join(Salon_.owner, JoinType.LEFT).get(User_.id)
                })
            }
        }
        return specification
    }
}
