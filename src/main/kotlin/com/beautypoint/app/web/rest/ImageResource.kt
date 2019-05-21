package com.beautypoint.app.web.rest


import com.beautypoint.app.domain.Image
import com.beautypoint.app.service.ImageQueryService
import com.beautypoint.app.service.ImageService
import com.beautypoint.app.service.dto.ImageCriteria
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
 * REST controller for managing [com.beautypoint.app.domain.Image].
 */
@RestController
@RequestMapping("/api")
class ImageResource(
    val imageService: ImageService,
    val imageQueryService: ImageQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /images` : Create a new image.
     *
     * @param image the image to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new image, or with status `400 (Bad Request)` if the image has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/images")
    fun createImage(@Valid @RequestBody image: Image): ResponseEntity<Image> {
        log.debug("REST request to save Image : {}", image)
        if (image.id != null) {
            throw BadRequestAlertException("A new image cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = imageService.save(image)
        return ResponseEntity.created(URI("/api/images/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /images` : Updates an existing image.
     *
     * @param image the image to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated image,
     * or with status `400 (Bad Request)` if the image is not valid,
     * or with status `500 (Internal Server Error)` if the image couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/images")
    fun updateImage(@Valid @RequestBody image: Image): ResponseEntity<Image> {
        log.debug("REST request to update Image : {}", image)
        if (image.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = imageService.save(image)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, image.id.toString()))
            .body(result)
    }

    /**
     * `GET  /images` : get all the images.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of images in body.
     */
    @GetMapping("/images")
    fun getAllImages(criteria: ImageCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Image>> {
        log.debug("REST request to get Images by criteria: {}", criteria)
        val page = imageQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /images/count}` : count all the images.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/images/count")
    fun countImages(criteria: ImageCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Images by criteria: {}", criteria)
        return ResponseEntity.ok().body(imageQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /images/:id` : get the "id" image.
     *
     * @param id the id of the image to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the image, or with status `404 (Not Found)`.
     */
    @GetMapping("/images/{id}")
    fun getImage(@PathVariable id: Long): ResponseEntity<Image> {
        log.debug("REST request to get Image : {}", id)
        val image = imageService.findOne(id)
        return ResponseUtil.wrapOrNotFound(image)
    }

    /**
     * `DELETE  /images/:id` : delete the "id" image.
     *
     * @param id the id of the image to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/images/{id}")
    fun deleteImage(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Image : {}", id)
        imageService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/images?query=:query` : search for the image corresponding
     * to the query.
     *
     * @param query the query of the image search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/images")
    fun searchImages(@RequestParam query: String, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<Image>> {
        log.debug("REST request to search for a page of Images for query {}", query)
        val page = imageService.search(query, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }


    companion object {
        private const val ENTITY_NAME = "image"
    }
}
