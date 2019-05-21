package com.beautypoint.app.service

import com.beautypoint.app.domain.*
import com.beautypoint.app.repository.RecordRepository
import com.beautypoint.app.repository.search.RecordSearchRepository
import com.beautypoint.app.service.dto.RecordCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [Record] entities in the database.
 * The main input is a [RecordCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Record] or a [Page] of [Record] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class RecordQueryService(
    val recordRepository: RecordRepository,
    val recordSearchRepository: RecordSearchRepository
) : QueryService<Record>() {

    private val log = LoggerFactory.getLogger(RecordQueryService::class.java)

    /**
     * Return a [MutableList] of [Record] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: RecordCriteria?): MutableList<Record> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return recordRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Record] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: RecordCriteria?, page: Pageable): Page<Record> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return recordRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: RecordCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return recordRepository.count(specification)
    }

    /**
     * Function to convert [RecordCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: RecordCriteria?): Specification<Record?> {
        var specification: Specification<Record?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, Record_.id))
            }
            if (criteria.bookingTime != null) {
                specification = specification.and(buildRangeSpecification(criteria.bookingTime, Record_.bookingTime))
            }
            if (criteria.duration != null) {
                specification = specification.and(buildRangeSpecification(criteria.duration, Record_.duration))
            }
            if (criteria.totalPrice != null) {
                specification = specification.and(buildRangeSpecification(criteria.totalPrice, Record_.totalPrice))
            }
            if (criteria.orderStatus != null) {
                specification = specification.and(buildSpecification(criteria.orderStatus, Record_.orderStatus))
            }
            if (criteria.comment != null) {
                specification = specification.and(buildStringSpecification(criteria.comment, Record_.comment))
            }
            if (criteria.masterId != null) {
                specification = specification.and(buildSpecification(criteria.masterId) {
                    it.join(Record_.master, JoinType.LEFT).get(Master_.id)
                })
            }
            if (criteria.variantId != null) {
                specification = specification.and(buildSpecification(criteria.variantId) {
                    it.join(Record_.variant, JoinType.LEFT).get(Variant_.id)
                })
            }
            if (criteria.optionId != null) {
                specification = specification.and(buildSpecification(criteria.optionId) {
                    it.join(Record_.options, JoinType.LEFT).get(Option_.id)
                })
            }
            if (criteria.userId != null) {
                specification = specification.and(buildSpecification(criteria.userId) {
                    it.join(Record_.user, JoinType.LEFT).get(User_.id)
                })
            }
            if (criteria.salonId != null) {
                specification = specification.and(buildSpecification(criteria.salonId) {
                    it.join(Record_.salon, JoinType.LEFT).get(Salon_.id)
                })
            }
        }
        return specification
    }
}
