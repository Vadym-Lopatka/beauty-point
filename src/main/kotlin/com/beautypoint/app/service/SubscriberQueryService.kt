package com.beautypoint.app.service

import com.beautypoint.app.domain.Subscriber
import com.beautypoint.app.domain.Subscriber_
import com.beautypoint.app.repository.SubscriberRepository
import com.beautypoint.app.repository.search.SubscriberSearchRepository
import com.beautypoint.app.service.dto.SubscriberCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for executing complex queries for [Subscriber] entities in the database.
 * The main input is a [SubscriberCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Subscriber] or a [Page] of [Subscriber] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class SubscriberQueryService(
    val subscriberRepository: SubscriberRepository,
    val subscriberSearchRepository: SubscriberSearchRepository
) : QueryService<Subscriber>() {

    private val log = LoggerFactory.getLogger(SubscriberQueryService::class.java)

    /**
     * Return a [MutableList] of [Subscriber] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: SubscriberCriteria?): MutableList<Subscriber> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return subscriberRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Subscriber] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: SubscriberCriteria?, page: Pageable): Page<Subscriber> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return subscriberRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: SubscriberCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return subscriberRepository.count(specification)
    }

    /**
     * Function to convert [SubscriberCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: SubscriberCriteria?): Specification<Subscriber?> {
        var specification: Specification<Subscriber?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, Subscriber_.id))
            }
            if (criteria.firsName != null) {
                specification = specification.and(buildStringSpecification(criteria.firsName, Subscriber_.firsName))
            }
            if (criteria.email != null) {
                specification = specification.and(buildStringSpecification(criteria.email, Subscriber_.email))
            }
        }
        return specification
    }
}
