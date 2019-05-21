package com.beautypoint.app.service

import com.beautypoint.app.domain.Variant
import com.beautypoint.app.repository.VariantRepository
import com.beautypoint.app.repository.search.VariantSearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Variant].
 */
@Service
@Transactional
class VariantService(
    val variantRepository: VariantRepository,
    val variantSearchRepository: VariantSearchRepository
) {

    private val log = LoggerFactory.getLogger(VariantService::class.java)

    /**
     * Save a variant.
     *
     * @param variant the entity to save.
     * @return the persisted entity.
     */
    fun save(variant: Variant): Variant {
        log.debug("Request to save Variant : {}", variant)
        val result = variantRepository.save(variant)
        variantSearchRepository.save(result)
        return result
    }

    /**
     * Get all the variants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Variant> {
        log.debug("Request to get all Variants")
        return variantRepository.findAll(pageable)
    }

    /**
     * Get all the variants with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Variant> {
        return variantRepository.findAllWithEagerRelationships(pageable)
    }

    /**
     * Get one variant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Variant> {
        log.debug("Request to get Variant : {}", id)
        return variantRepository.findOneWithEagerRelationships(id)
    }

    /**
     * Delete the variant by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Variant : {}", id)

        variantRepository.deleteById(id)
        variantSearchRepository.deleteById(id)
    }

    /**
     * Search for the variant corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<Variant> {
        log.debug("Request to search for a page of Variants for query {}", query)
        return variantSearchRepository.search(queryStringQuery(query), pageable)
    }
}
