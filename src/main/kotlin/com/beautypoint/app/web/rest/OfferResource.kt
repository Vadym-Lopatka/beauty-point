package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.Offer
import com.beautypoint.app.service.OfferQueryService
import com.beautypoint.app.service.OfferService
import com.beautypoint.app.service.dto.OfferCriteria
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
 * REST controller for managing [com.beautypoint.app.domain.Offer].
 */
@RestController
@RequestMapping("/api")
class OfferResource(
    val offerService: OfferService,
    val offerQueryService: OfferQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /offers` : Create a new offer.
     *
     * @param offer the offer to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new offer, or with status `400 (Bad Request)` if the offer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/offers")
    fun createOffer(@Valid @RequestBody offer: Offer): ResponseEntity<Offer> {
        log.debug("REST request to save Offer : {}", offer)
        if (offer.id != null) {
            throw BadRequestAlertException("A new offer cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = offerService.save(offer)
        return ResponseEntity.created(URI("/api/offers/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /offers` : Updates an existing offer.
     *
     * @param offer the offer to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated offer,
     * or with status `400 (Bad Request)` if the offer is not valid,
     * or with status `500 (Internal Server Error)` if the offer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/offers")
    fun updateOffer(@Valid @RequestBody offer: Offer): ResponseEntity<Offer> {
        log.debug("REST request to update Offer : {}", offer)
        if (offer.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = offerService.save(offer)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, offer.id.toString()))
            .body(result)
    }

    /**
     * `GET  /offers` : get all the offers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of offers in body.
     */
    @GetMapping("/offers")
    fun getAllOffers(criteria: OfferCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Offer>> {
        log.debug("REST request to get Offers by criteria: {}", criteria)
        val page = offerQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /offers/count}` : count all the offers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/offers/count")
    fun countOffers(criteria: OfferCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Offers by criteria: {}", criteria)
        return ResponseEntity.ok().body(offerQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /offers/:id` : get the "id" offer.
     *
     * @param id the id of the offer to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the offer, or with status `404 (Not Found)`.
     */
    @GetMapping("/offers/{id}")
    fun getOffer(@PathVariable id: Long): ResponseEntity<Offer> {
        log.debug("REST request to get Offer : {}", id)
        val offer = offerService.findOne(id)
        return ResponseUtil.wrapOrNotFound(offer)
    }

    /**
     * `DELETE  /offers/:id` : delete the "id" offer.
     *
     * @param id the id of the offer to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/offers/{id}")
    fun deleteOffer(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Offer : {}", id)
        offerService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/offers?query=:query` : search for the offer corresponding
     * to the query.
     *
     * @param query the query of the offer search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/offers")
    fun searchOffers(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Offer>> {
        log.debug("REST request to search for a page of Offers for query {}", query)
        val page = offerService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "offer"
    }
}
