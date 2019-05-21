package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.Variant
import com.beautypoint.app.service.VariantQueryService
import com.beautypoint.app.service.VariantService
import com.beautypoint.app.service.dto.VariantCriteria
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
 * REST controller for managing [com.beautypoint.app.domain.Variant].
 */
@RestController
@RequestMapping("/api")
class VariantResource(
    val variantService: VariantService,
    val variantQueryService: VariantQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /variants` : Create a new variant.
     *
     * @param variant the variant to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new variant, or with status `400 (Bad Request)` if the variant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/variants")
    fun createVariant(@Valid @RequestBody variant: Variant): ResponseEntity<Variant> {
        log.debug("REST request to save Variant : {}", variant)
        if (variant.id != null) {
            throw BadRequestAlertException("A new variant cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = variantService.save(variant)
        return ResponseEntity.created(URI("/api/variants/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /variants` : Updates an existing variant.
     *
     * @param variant the variant to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated variant,
     * or with status `400 (Bad Request)` if the variant is not valid,
     * or with status `500 (Internal Server Error)` if the variant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/variants")
    fun updateVariant(@Valid @RequestBody variant: Variant): ResponseEntity<Variant> {
        log.debug("REST request to update Variant : {}", variant)
        if (variant.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = variantService.save(variant)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, variant.id.toString()))
            .body(result)
    }

    /**
     * `GET  /variants` : get all the variants.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of variants in body.
     */
    @GetMapping("/variants")
    fun getAllVariants(criteria: VariantCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Variant>> {
        log.debug("REST request to get Variants by criteria: {}", criteria)
        val page = variantQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /variants/count}` : count all the variants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/variants/count")
    fun countVariants(criteria: VariantCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Variants by criteria: {}", criteria)
        return ResponseEntity.ok().body(variantQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /variants/:id` : get the "id" variant.
     *
     * @param id the id of the variant to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the variant, or with status `404 (Not Found)`.
     */
    @GetMapping("/variants/{id}")
    fun getVariant(@PathVariable id: Long): ResponseEntity<Variant> {
        log.debug("REST request to get Variant : {}", id)
        val variant = variantService.findOne(id)
        return ResponseUtil.wrapOrNotFound(variant)
    }

    /**
     * `DELETE  /variants/:id` : delete the "id" variant.
     *
     * @param id the id of the variant to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/variants/{id}")
    fun deleteVariant(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Variant : {}", id)
        variantService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/variants?query=:query` : search for the variant corresponding
     * to the query.
     *
     * @param query the query of the variant search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/variants")
    fun searchVariants(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Variant>> {
        log.debug("REST request to search for a page of Variants for query {}", query)
        val page = variantService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "variant"
    }
}
