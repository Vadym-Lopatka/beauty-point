package com.beautypoint.app.service

import com.beautypoint.app.domain.Salon
import com.beautypoint.app.repository.SalonRepository
import com.beautypoint.app.repository.search.SalonSearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Salon].
 */
@Service
@Transactional
class SalonService(
    val salonRepository: SalonRepository,
    val salonSearchRepository: SalonSearchRepository
) {

    private val log = LoggerFactory.getLogger(SalonService::class.java)

    /**
     * Save a salon.
     *
     * @param salon the entity to save.
     * @return the persisted entity.
     */
    fun save(salon: Salon): Salon {
        log.debug("Request to save Salon : {}", salon)
        val result = salonRepository.save(salon)
        salonSearchRepository.save(result)
        return result
    }

    /**
     * Get all the salons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Salon> {
        log.debug("Request to get all Salons")
        return salonRepository.findAll(pageable)
    }

    /**
     * Get all the salons with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Salon> {
        return salonRepository.findAllWithEagerRelationships(pageable)
    }

    /**
     * Get one salon by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Salon> {
        log.debug("Request to get Salon : {}", id)
        return salonRepository.findOneWithEagerRelationships(id)
    }

    /**
     * Delete the salon by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Salon : {}", id)

        salonRepository.deleteById(id)
        salonSearchRepository.deleteById(id)
    }

    /**
     * Search for the salon corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<Salon> {
        log.debug("Request to search for a page of Salons for query {}", query)
        return salonSearchRepository.search(queryStringQuery(query), pageable)
    }
}
