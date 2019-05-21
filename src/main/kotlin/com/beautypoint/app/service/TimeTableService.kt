package com.beautypoint.app.service

import com.beautypoint.app.domain.TimeTable
import com.beautypoint.app.repository.TimeTableRepository
import com.beautypoint.app.repository.search.TimeTableSearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [TimeTable].
 */
@Service
@Transactional
class TimeTableService(
    val timeTableRepository: TimeTableRepository,
    val timeTableSearchRepository: TimeTableSearchRepository
) {

    private val log = LoggerFactory.getLogger(TimeTableService::class.java)

    /**
     * Save a timeTable.
     *
     * @param timeTable the entity to save.
     * @return the persisted entity.
     */
    fun save(timeTable: TimeTable): TimeTable {
        log.debug("Request to save TimeTable : {}", timeTable)
        val result = timeTableRepository.save(timeTable)
        timeTableSearchRepository.save(result)
        return result
    }

    /**
     * Get all the timeTables.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<TimeTable> {
        log.debug("Request to get all TimeTables")
        return timeTableRepository.findAll(pageable)
    }

    /**
     * Get one timeTable by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<TimeTable> {
        log.debug("Request to get TimeTable : {}", id)
        return timeTableRepository.findById(id)
    }

    /**
     * Delete the timeTable by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete TimeTable : {}", id)

        timeTableRepository.deleteById(id)
        timeTableSearchRepository.deleteById(id)
    }

    /**
     * Search for the timeTable corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<TimeTable> {
        log.debug("Request to search for a page of TimeTables for query {}", query)
        return timeTableSearchRepository.search(queryStringQuery(query), pageable)
    }
}
