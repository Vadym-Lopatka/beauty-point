package com.beautypoint.app.service

import com.beautypoint.app.domain.Subscriber
import com.beautypoint.app.repository.SubscriberRepository
import com.beautypoint.app.repository.search.SubscriberSearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Subscriber].
 */
@Service
@Transactional
class SubscriberService(
    val subscriberRepository: SubscriberRepository,
    val subscriberSearchRepository: SubscriberSearchRepository
) {

    private val log = LoggerFactory.getLogger(SubscriberService::class.java)

    /**
     * Save a subscriber.
     *
     * @param subscriber the entity to save.
     * @return the persisted entity.
     */
    fun save(subscriber: Subscriber): Subscriber {
        log.debug("Request to save Subscriber : {}", subscriber)
        val result = subscriberRepository.save(subscriber)
        subscriberSearchRepository.save(result)
        return result
    }

    /**
     * Get all the subscribers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Subscriber> {
        log.debug("Request to get all Subscribers")
        return subscriberRepository.findAll(pageable)
    }

    /**
     * Get one subscriber by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Subscriber> {
        log.debug("Request to get Subscriber : {}", id)
        return subscriberRepository.findById(id)
    }

    /**
     * Delete the subscriber by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Subscriber : {}", id)

        subscriberRepository.deleteById(id)
        subscriberSearchRepository.deleteById(id)
    }

    /**
     * Search for the subscriber corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<Subscriber> {
        log.debug("Request to search for a page of Subscribers for query {}", query)
        return subscriberSearchRepository.search(queryStringQuery(query), pageable)
    }
}
