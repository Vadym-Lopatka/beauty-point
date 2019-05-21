package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.Category
import com.beautypoint.app.service.CategoryQueryService
import com.beautypoint.app.service.CategoryService
import com.beautypoint.app.service.dto.CategoryCriteria
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
 * REST controller for managing [com.beautypoint.app.domain.Category].
 */
@RestController
@RequestMapping("/api")
class CategoryResource(
    val categoryService: CategoryService,
    val categoryQueryService: CategoryQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /categories` : Create a new category.
     *
     * @param category the category to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new category, or with status `400 (Bad Request)` if the category has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/categories")
    fun createCategory(@Valid @RequestBody category: Category): ResponseEntity<Category> {
        log.debug("REST request to save Category : {}", category)
        if (category.id != null) {
            throw BadRequestAlertException("A new category cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = categoryService.save(category)
        return ResponseEntity.created(URI("/api/categories/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /categories` : Updates an existing category.
     *
     * @param category the category to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated category,
     * or with status `400 (Bad Request)` if the category is not valid,
     * or with status `500 (Internal Server Error)` if the category couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/categories")
    fun updateCategory(@Valid @RequestBody category: Category): ResponseEntity<Category> {
        log.debug("REST request to update Category : {}", category)
        if (category.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = categoryService.save(category)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, category.id.toString()))
            .body(result)
    }

    /**
     * `GET  /categories` : get all the categories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of categories in body.
     */
    @GetMapping("/categories")
    fun getAllCategories(criteria: CategoryCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Category>> {
        log.debug("REST request to get Categories by criteria: {}", criteria)
        val page = categoryQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /categories/count}` : count all the categories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/categories/count")
    fun countCategories(criteria: CategoryCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Categories by criteria: {}", criteria)
        return ResponseEntity.ok().body(categoryQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /categories/:id` : get the "id" category.
     *
     * @param id the id of the category to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the category, or with status `404 (Not Found)`.
     */
    @GetMapping("/categories/{id}")
    fun getCategory(@PathVariable id: Long): ResponseEntity<Category> {
        log.debug("REST request to get Category : {}", id)
        val category = categoryService.findOne(id)
        return ResponseUtil.wrapOrNotFound(category)
    }

    /**
     * `DELETE  /categories/:id` : delete the "id" category.
     *
     * @param id the id of the category to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/categories/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Category : {}", id)
        categoryService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/categories?query=:query` : search for the category corresponding
     * to the query.
     *
     * @param query the query of the category search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/categories")
    fun searchCategories(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Category>> {
        log.debug("REST request to search for a page of Categories for query {}", query)
        val page = categoryService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "category"
    }
}
