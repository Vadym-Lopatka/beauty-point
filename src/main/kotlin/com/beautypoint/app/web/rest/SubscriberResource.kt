package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.Subscriber
import com.beautypoint.app.service.SubscriberQueryService
import com.beautypoint.app.service.SubscriberService
import com.beautypoint.app.service.dto.SubscriberCriteria
import com.beautypoint.app.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid

/**
 * REST controller for managing [com.beautypoint.app.domain.Subscriber].
 */
@RestController
@RequestMapping("/api")
class SubscriberResource(
    val subscriberService: SubscriberService,
    val subscriberQueryService: SubscriberQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /subscribers` : Create a new subscriber.
     *
     * @param subscriber the subscriber to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new subscriber, or with status `400 (Bad Request)` if the subscriber has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/subscribers")
    fun createSubscriber(@Valid @RequestBody subscriber: Subscriber): ResponseEntity<Subscriber> {
        log.debug("REST request to save Subscriber : {}", subscriber)
        if (subscriber.id != null) {
            throw BadRequestAlertException("A new subscriber cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = subscriberService.save(subscriber)
        return ResponseEntity.created(URI("/api/subscribers/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /subscribers` : Updates an existing subscriber.
     *
     * @param subscriber the subscriber to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated subscriber,
     * or with status `400 (Bad Request)` if the subscriber is not valid,
     * or with status `500 (Internal Server Error)` if the subscriber couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/subscribers")
    fun updateSubscriber(@Valid @RequestBody subscriber: Subscriber): ResponseEntity<Subscriber> {
        log.debug("REST request to update Subscriber : {}", subscriber)
        if (subscriber.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = subscriberService.save(subscriber)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscriber.id.toString()))
            .body(result)
    }

    /**
     * `GET  /subscribers` : get all the subscribers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of subscribers in body.
     */
    @GetMapping("/subscribers")
    fun getAllSubscribers(criteria: SubscriberCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Subscriber>> {
        log.debug("REST request to get Subscribers by criteria: {}", criteria)
        val page = subscriberQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /subscribers/count}` : count all the subscribers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/subscribers/count")
    fun countSubscribers(criteria: SubscriberCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Subscribers by criteria: {}", criteria)
        return ResponseEntity.ok().body(subscriberQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /subscribers/:id` : get the "id" subscriber.
     *
     * @param id the id of the subscriber to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the subscriber, or with status `404 (Not Found)`.
     */
    @GetMapping("/subscribers/{id}")
    fun getSubscriber(@PathVariable id: Long): ResponseEntity<Subscriber> {
        log.debug("REST request to get Subscriber : {}", id)
        val subscriber = subscriberService.findOne(id)
        return ResponseUtil.wrapOrNotFound(subscriber)
    }

    /**
     * `DELETE  /subscribers/:id` : delete the "id" subscriber.
     *
     * @param id the id of the subscriber to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/subscribers/{id}")
    fun deleteSubscriber(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Subscriber : {}", id)
        subscriberService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/subscribers?query=:query` : search for the subscriber corresponding
     * to the query.
     *
     * @param query the query of the subscriber search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/subscribers")
    fun searchSubscribers(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Subscriber>> {
        log.debug("REST request to search for a page of Subscribers for query {}", query)
        val page = subscriberService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "subscriber"
    }
}
