package com.beautypoint.app.service

import com.beautypoint.app.domain.Option
import com.beautypoint.app.repository.OptionRepository
import com.beautypoint.app.repository.search.OptionSearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Option].
 */
@Service
@Transactional
class OptionService(
    val optionRepository: OptionRepository,
    val optionSearchRepository: OptionSearchRepository
) {

    private val log = LoggerFactory.getLogger(OptionService::class.java)

    /**
     * Save a option.
     *
     * @param option the entity to save.
     * @return the persisted entity.
     */
    fun save(option: Option): Option {
        log.debug("Request to save Option : {}", option)
        val result = optionRepository.save(option)
        optionSearchRepository.save(result)
        return result
    }

    /**
     * Get all the options.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Option> {
        log.debug("Request to get all Options")
        return optionRepository.findAll(pageable)
    }

    /**
     * Get one option by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Option> {
        log.debug("Request to get Option : {}", id)
        return optionRepository.findById(id)
    }

    /**
     * Delete the option by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Option : {}", id)

        optionRepository.deleteById(id)
        optionSearchRepository.deleteById(id)
    }

    /**
     * Search for the option corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<Option> {
        log.debug("Request to search for a page of Options for query {}", query)
        return optionSearchRepository.search(queryStringQuery(query), pageable)
    }
}
