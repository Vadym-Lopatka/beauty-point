package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.Salon
import com.beautypoint.app.service.SalonQueryService
import com.beautypoint.app.service.SalonService
import com.beautypoint.app.service.dto.SalonCriteria
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
 * REST controller for managing [com.beautypoint.app.domain.Salon].
 */
@RestController
@RequestMapping("/api")
class SalonResource(
    val salonService: SalonService,
    val salonQueryService: SalonQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /salons` : Create a new salon.
     *
     * @param salon the salon to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new salon, or with status `400 (Bad Request)` if the salon has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/salons")
    fun createSalon(@Valid @RequestBody salon: Salon): ResponseEntity<Salon> {
        log.debug("REST request to save Salon : {}", salon)
        if (salon.id != null) {
            throw BadRequestAlertException("A new salon cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = salonService.save(salon)
        return ResponseEntity.created(URI("/api/salons/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /salons` : Updates an existing salon.
     *
     * @param salon the salon to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated salon,
     * or with status `400 (Bad Request)` if the salon is not valid,
     * or with status `500 (Internal Server Error)` if the salon couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/salons")
    fun updateSalon(@Valid @RequestBody salon: Salon): ResponseEntity<Salon> {
        log.debug("REST request to update Salon : {}", salon)
        if (salon.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = salonService.save(salon)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, salon.id.toString()))
            .body(result)
    }

    /**
     * `GET  /salons` : get all the salons.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of salons in body.
     */
    @GetMapping("/salons")
    fun getAllSalons(criteria: SalonCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Salon>> {
        log.debug("REST request to get Salons by criteria: {}", criteria)
        val page = salonQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /salons/count}` : count all the salons.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/salons/count")
    fun countSalons(criteria: SalonCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Salons by criteria: {}", criteria)
        return ResponseEntity.ok().body(salonQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /salons/:id` : get the "id" salon.
     *
     * @param id the id of the salon to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the salon, or with status `404 (Not Found)`.
     */
    @GetMapping("/salons/{id}")
    fun getSalon(@PathVariable id: Long): ResponseEntity<Salon> {
        log.debug("REST request to get Salon : {}", id)
        val salon = salonService.findOne(id)
        return ResponseUtil.wrapOrNotFound(salon)
    }

    /**
     * `DELETE  /salons/:id` : delete the "id" salon.
     *
     * @param id the id of the salon to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/salons/{id}")
    fun deleteSalon(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Salon : {}", id)
        salonService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/salons?query=:query` : search for the salon corresponding
     * to the query.
     *
     * @param query the query of the salon search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/salons")
    fun searchSalons(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Salon>> {
        log.debug("REST request to search for a page of Salons for query {}", query)
        val page = salonService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "salon"
    }
}
