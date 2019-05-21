package com.beautypoint.app.service

import com.beautypoint.app.domain.Master
import com.beautypoint.app.repository.MasterRepository
import com.beautypoint.app.repository.search.MasterSearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Master].
 */
@Service
@Transactional
class MasterService(
    val masterRepository: MasterRepository,
    val masterSearchRepository: MasterSearchRepository
) {

    private val log = LoggerFactory.getLogger(MasterService::class.java)

    /**
     * Save a master.
     *
     * @param master the entity to save.
     * @return the persisted entity.
     */
    fun save(master: Master): Master {
        log.debug("Request to save Master : {}", master)
        val result = masterRepository.save(master)
        masterSearchRepository.save(result)
        return result
    }

    /**
     * Get all the masters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Master> {
        log.debug("Request to get all Masters")
        return masterRepository.findAll(pageable)
    }

    /**
     * Get all the masters with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Master> {
        return masterRepository.findAllWithEagerRelationships(pageable)
    }

    /**
     * Get one master by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Master> {
        log.debug("Request to get Master : {}", id)
        return masterRepository.findOneWithEagerRelationships(id)
    }

    /**
     * Delete the master by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Master : {}", id)

        masterRepository.deleteById(id)
        masterSearchRepository.deleteById(id)
    }

    /**
     * Search for the master corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<Master> {
        log.debug("Request to search for a page of Masters for query {}", query)
        return masterSearchRepository.search(queryStringQuery(query), pageable)
    }
}
