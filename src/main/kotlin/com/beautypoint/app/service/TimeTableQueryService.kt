package com.beautypoint.app.service

import com.beautypoint.app.domain.TimeTable
import com.beautypoint.app.domain.TimeTable_
import com.beautypoint.app.repository.TimeTableRepository
import com.beautypoint.app.repository.search.TimeTableSearchRepository
import com.beautypoint.app.service.dto.TimeTableCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for executing complex queries for [TimeTable] entities in the database.
 * The main input is a [TimeTableCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [TimeTable] or a [Page] of [TimeTable] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class TimeTableQueryService(
    val timeTableRepository: TimeTableRepository,
    val timeTableSearchRepository: TimeTableSearchRepository
) : QueryService<TimeTable>() {

    private val log = LoggerFactory.getLogger(TimeTableQueryService::class.java)

    /**
     * Return a [MutableList] of [TimeTable] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: TimeTableCriteria?): MutableList<TimeTable> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return timeTableRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [TimeTable] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: TimeTableCriteria?, page: Pageable): Page<TimeTable> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return timeTableRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: TimeTableCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return timeTableRepository.count(specification)
    }

    /**
     * Function to convert [TimeTableCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: TimeTableCriteria?): Specification<TimeTable?> {
        var specification: Specification<TimeTable?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, TimeTable_.id))
            }
            if (criteria.everyDayEqual != null) {
                specification = specification.and(buildSpecification(criteria.everyDayEqual, TimeTable_.everyDayEqual))
            }
            if (criteria.mo != null) {
                specification = specification.and(buildRangeSpecification(criteria.mo, TimeTable_.mo))
            }
            if (criteria.tu != null) {
                specification = specification.and(buildRangeSpecification(criteria.tu, TimeTable_.tu))
            }
            if (criteria.we != null) {
                specification = specification.and(buildRangeSpecification(criteria.we, TimeTable_.we))
            }
            if (criteria.th != null) {
                specification = specification.and(buildRangeSpecification(criteria.th, TimeTable_.th))
            }
            if (criteria.fr != null) {
                specification = specification.and(buildRangeSpecification(criteria.fr, TimeTable_.fr))
            }
            if (criteria.sa != null) {
                specification = specification.and(buildRangeSpecification(criteria.sa, TimeTable_.sa))
            }
            if (criteria.su != null) {
                specification = specification.and(buildRangeSpecification(criteria.su, TimeTable_.su))
            }
        }
        return specification
    }
}
