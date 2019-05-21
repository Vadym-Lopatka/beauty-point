package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.Option
import com.beautypoint.app.service.OptionQueryService
import com.beautypoint.app.service.OptionService
import com.beautypoint.app.service.dto.OptionCriteria
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
 * REST controller for managing [com.beautypoint.app.domain.Option].
 */
@RestController
@RequestMapping("/api")
class OptionResource(
    val optionService: OptionService,
    val optionQueryService: OptionQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /options` : Create a new option.
     *
     * @param option the option to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new option, or with status `400 (Bad Request)` if the option has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/options")
    fun createOption(@Valid @RequestBody option: Option): ResponseEntity<Option> {
        log.debug("REST request to save Option : {}", option)
        if (option.id != null) {
            throw BadRequestAlertException("A new option cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = optionService.save(option)
        return ResponseEntity.created(URI("/api/options/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /options` : Updates an existing option.
     *
     * @param option the option to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated option,
     * or with status `400 (Bad Request)` if the option is not valid,
     * or with status `500 (Internal Server Error)` if the option couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/options")
    fun updateOption(@Valid @RequestBody option: Option): ResponseEntity<Option> {
        log.debug("REST request to update Option : {}", option)
        if (option.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = optionService.save(option)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, option.id.toString()))
            .body(result)
    }

    /**
     * `GET  /options` : get all the options.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of options in body.
     */
    @GetMapping("/options")
    fun getAllOptions(criteria: OptionCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Option>> {
        log.debug("REST request to get Options by criteria: {}", criteria)
        val page = optionQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /options/count}` : count all the options.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/options/count")
    fun countOptions(criteria: OptionCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Options by criteria: {}", criteria)
        return ResponseEntity.ok().body(optionQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /options/:id` : get the "id" option.
     *
     * @param id the id of the option to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the option, or with status `404 (Not Found)`.
     */
    @GetMapping("/options/{id}")
    fun getOption(@PathVariable id: Long): ResponseEntity<Option> {
        log.debug("REST request to get Option : {}", id)
        val option = optionService.findOne(id)
        return ResponseUtil.wrapOrNotFound(option)
    }

    /**
     * `DELETE  /options/:id` : delete the "id" option.
     *
     * @param id the id of the option to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/options/{id}")
    fun deleteOption(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Option : {}", id)
        optionService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/options?query=:query` : search for the option corresponding
     * to the query.
     *
     * @param query the query of the option search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/options")
    fun searchOptions(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Option>> {
        log.debug("REST request to search for a page of Options for query {}", query)
        val page = optionService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "option"
    }
}
