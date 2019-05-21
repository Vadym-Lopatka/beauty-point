package com.beautypoint.app.service

import com.beautypoint.app.domain.Record
import com.beautypoint.app.repository.RecordRepository
import com.beautypoint.app.repository.search.RecordSearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Record].
 */
@Service
@Transactional
class RecordService(
    val recordRepository: RecordRepository,
    val recordSearchRepository: RecordSearchRepository
) {

    private val log = LoggerFactory.getLogger(RecordService::class.java)

    /**
     * Save a record.
     *
     * @param record the entity to save.
     * @return the persisted entity.
     */
    fun save(record: Record): Record {
        log.debug("Request to save Record : {}", record)
        val result = recordRepository.save(record)
        recordSearchRepository.save(result)
        return result
    }

    /**
     * Get all the records.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Record> {
        log.debug("Request to get all Records")
        return recordRepository.findAll(pageable)
    }

    /**
     * Get all the records with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Record> {
        return recordRepository.findAllWithEagerRelationships(pageable)
    }

    /**
     * Get one record by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Record> {
        log.debug("Request to get Record : {}", id)
        return recordRepository.findOneWithEagerRelationships(id)
    }

    /**
     * Delete the record by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Record : {}", id)

        recordRepository.deleteById(id)
        recordSearchRepository.deleteById(id)
    }

    /**
     * Search for the record corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<Record> {
        log.debug("Request to search for a page of Records for query {}", query)
        return recordSearchRepository.search(queryStringQuery(query), pageable)
    }
}
