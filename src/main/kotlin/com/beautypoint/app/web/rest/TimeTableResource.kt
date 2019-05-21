package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.TimeTable
import com.beautypoint.app.service.TimeTableQueryService
import com.beautypoint.app.service.TimeTableService
import com.beautypoint.app.service.dto.TimeTableCriteria
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
 * REST controller for managing [com.beautypoint.app.domain.TimeTable].
 */
@RestController
@RequestMapping("/api")
class TimeTableResource(
    val timeTableService: TimeTableService,
    val timeTableQueryService: TimeTableQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /time-tables` : Create a new timeTable.
     *
     * @param timeTable the timeTable to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new timeTable, or with status `400 (Bad Request)` if the timeTable has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/time-tables")
    fun createTimeTable(@Valid @RequestBody timeTable: TimeTable): ResponseEntity<TimeTable> {
        log.debug("REST request to save TimeTable : {}", timeTable)
        if (timeTable.id != null) {
            throw BadRequestAlertException("A new timeTable cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = timeTableService.save(timeTable)
        return ResponseEntity.created(URI("/api/time-tables/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /time-tables` : Updates an existing timeTable.
     *
     * @param timeTable the timeTable to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated timeTable,
     * or with status `400 (Bad Request)` if the timeTable is not valid,
     * or with status `500 (Internal Server Error)` if the timeTable couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/time-tables")
    fun updateTimeTable(@Valid @RequestBody timeTable: TimeTable): ResponseEntity<TimeTable> {
        log.debug("REST request to update TimeTable : {}", timeTable)
        if (timeTable.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = timeTableService.save(timeTable)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeTable.id.toString()))
            .body(result)
    }

    /**
     * `GET  /time-tables` : get all the timeTables.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of timeTables in body.
     */
    @GetMapping("/time-tables")
    fun getAllTimeTables(criteria: TimeTableCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<TimeTable>> {
        log.debug("REST request to get TimeTables by criteria: {}", criteria)
        val page = timeTableQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /time-tables/count}` : count all the timeTables.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/time-tables/count")
    fun countTimeTables(criteria: TimeTableCriteria): ResponseEntity<Long> {
        log.debug("REST request to count TimeTables by criteria: {}", criteria)
        return ResponseEntity.ok().body(timeTableQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /time-tables/:id` : get the "id" timeTable.
     *
     * @param id the id of the timeTable to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the timeTable, or with status `404 (Not Found)`.
     */
    @GetMapping("/time-tables/{id}")
    fun getTimeTable(@PathVariable id: Long): ResponseEntity<TimeTable> {
        log.debug("REST request to get TimeTable : {}", id)
        val timeTable = timeTableService.findOne(id)
        return ResponseUtil.wrapOrNotFound(timeTable)
    }

    /**
     * `DELETE  /time-tables/:id` : delete the "id" timeTable.
     *
     * @param id the id of the timeTable to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/time-tables/{id}")
    fun deleteTimeTable(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete TimeTable : {}", id)
        timeTableService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/time-tables?query=:query` : search for the timeTable corresponding
     * to the query.
     *
     * @param query the query of the timeTable search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/time-tables")
    fun searchTimeTables(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<TimeTable>> {
        log.debug("REST request to search for a page of TimeTables for query {}", query)
        val page = timeTableService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "timeTable"
    }
}
