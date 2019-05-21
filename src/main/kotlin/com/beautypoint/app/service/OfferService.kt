package com.beautypoint.app.service

import com.beautypoint.app.domain.Offer
import com.beautypoint.app.repository.OfferRepository
import com.beautypoint.app.repository.search.OfferSearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Offer].
 */
@Service
@Transactional
class OfferService(
    val offerRepository: OfferRepository,
    val offerSearchRepository: OfferSearchRepository
) {

    private val log = LoggerFactory.getLogger(OfferService::class.java)

    /**
     * Save a offer.
     *
     * @param offer the entity to save.
     * @return the persisted entity.
     */
    fun save(offer: Offer): Offer {
        log.debug("Request to save Offer : {}", offer)
        val result = offerRepository.save(offer)
        offerSearchRepository.save(result)
        return result
    }

    /**
     * Get all the offers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Offer> {
        log.debug("Request to get all Offers")
        return offerRepository.findAll(pageable)
    }

    /**
     * Get one offer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Offer> {
        log.debug("Request to get Offer : {}", id)
        return offerRepository.findById(id)
    }

    /**
     * Delete the offer by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Offer : {}", id)

        offerRepository.deleteById(id)
        offerSearchRepository.deleteById(id)
    }

    /**
     * Search for the offer corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<Offer> {
        log.debug("Request to search for a page of Offers for query {}", query)
        return offerSearchRepository.search(queryStringQuery(query), pageable)
    }
}
