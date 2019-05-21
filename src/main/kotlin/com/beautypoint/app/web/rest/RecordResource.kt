package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.Record
import com.beautypoint.app.service.RecordQueryService
import com.beautypoint.app.service.RecordService
import com.beautypoint.app.service.dto.RecordCriteria
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
 * REST controller for managing [com.beautypoint.app.domain.Record].
 */
@RestController
@RequestMapping("/api")
class RecordResource(
    val recordService: RecordService,
    val recordQueryService: RecordQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /records` : Create a new record.
     *
     * @param record the record to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new record, or with status `400 (Bad Request)` if the record has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/records")
    fun createRecord(@Valid @RequestBody record: Record): ResponseEntity<Record> {
        log.debug("REST request to save Record : {}", record)
        if (record.id != null) {
            throw BadRequestAlertException("A new record cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = recordService.save(record)
        return ResponseEntity.created(URI("/api/records/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /records` : Updates an existing record.
     *
     * @param record the record to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated record,
     * or with status `400 (Bad Request)` if the record is not valid,
     * or with status `500 (Internal Server Error)` if the record couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/records")
    fun updateRecord(@Valid @RequestBody record: Record): ResponseEntity<Record> {
        log.debug("REST request to update Record : {}", record)
        if (record.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = recordService.save(record)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, record.id.toString()))
            .body(result)
    }

    /**
     * `GET  /records` : get all the records.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of records in body.
     */
    @GetMapping("/records")
    fun getAllRecords(criteria: RecordCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Record>> {
        log.debug("REST request to get Records by criteria: {}", criteria)
        val page = recordQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /records/count}` : count all the records.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/records/count")
    fun countRecords(criteria: RecordCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Records by criteria: {}", criteria)
        return ResponseEntity.ok().body(recordQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /records/:id` : get the "id" record.
     *
     * @param id the id of the record to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the record, or with status `404 (Not Found)`.
     */
    @GetMapping("/records/{id}")
    fun getRecord(@PathVariable id: Long): ResponseEntity<Record> {
        log.debug("REST request to get Record : {}", id)
        val record = recordService.findOne(id)
        return ResponseUtil.wrapOrNotFound(record)
    }

    /**
     * `DELETE  /records/:id` : delete the "id" record.
     *
     * @param id the id of the record to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/records/{id}")
    fun deleteRecord(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Record : {}", id)
        recordService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/records?query=:query` : search for the record corresponding
     * to the query.
     *
     * @param query the query of the record search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/records")
    fun searchRecords(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Record>> {
        log.debug("REST request to search for a page of Records for query {}", query)
        val page = recordService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "record"
    }
}
