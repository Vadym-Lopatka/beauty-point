package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.Master
import com.beautypoint.app.service.MasterQueryService
import com.beautypoint.app.service.MasterService
import com.beautypoint.app.service.dto.MasterCriteria
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
 * REST controller for managing [com.beautypoint.app.domain.Master].
 */
@RestController
@RequestMapping("/api")
class MasterResource(
    val masterService: MasterService,
    val masterQueryService: MasterQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /masters` : Create a new master.
     *
     * @param master the master to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new master, or with status `400 (Bad Request)` if the master has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/masters")
    fun createMaster(@Valid @RequestBody master: Master): ResponseEntity<Master> {
        log.debug("REST request to save Master : {}", master)
        if (master.id != null) {
            throw BadRequestAlertException("A new master cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = masterService.save(master)
        return ResponseEntity.created(URI("/api/masters/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /masters` : Updates an existing master.
     *
     * @param master the master to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated master,
     * or with status `400 (Bad Request)` if the master is not valid,
     * or with status `500 (Internal Server Error)` if the master couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/masters")
    fun updateMaster(@Valid @RequestBody master: Master): ResponseEntity<Master> {
        log.debug("REST request to update Master : {}", master)
        if (master.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = masterService.save(master)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, master.id.toString()))
            .body(result)
    }

    /**
     * `GET  /masters` : get all the masters.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of masters in body.
     */
    @GetMapping("/masters")
    fun getAllMasters(criteria: MasterCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Master>> {
        log.debug("REST request to get Masters by criteria: {}", criteria)
        val page = masterQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /masters/count}` : count all the masters.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/masters/count")
    fun countMasters(criteria: MasterCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Masters by criteria: {}", criteria)
        return ResponseEntity.ok().body(masterQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /masters/:id` : get the "id" master.
     *
     * @param id the id of the master to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the master, or with status `404 (Not Found)`.
     */
    @GetMapping("/masters/{id}")
    fun getMaster(@PathVariable id: Long): ResponseEntity<Master> {
        log.debug("REST request to get Master : {}", id)
        val master = masterService.findOne(id)
        return ResponseUtil.wrapOrNotFound(master)
    }

    /**
     * `DELETE  /masters/:id` : delete the "id" master.
     *
     * @param id the id of the master to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/masters/{id}")
    fun deleteMaster(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Master : {}", id)
        masterService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/masters?query=:query` : search for the master corresponding
     * to the query.
     *
     * @param query the query of the master search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/masters")
    fun searchMasters(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Master>> {
        log.debug("REST request to search for a page of Masters for query {}", query)
        val page = masterService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "master"
    }
}
