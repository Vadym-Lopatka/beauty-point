package com.beautypoint.app.service

import com.beautypoint.app.domain.Image
import com.beautypoint.app.repository.ImageRepository
import com.beautypoint.app.repository.search.ImageSearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Image].
 */
@Service
@Transactional
class ImageService(
    val imageRepository: ImageRepository,
    val imageSearchRepository: ImageSearchRepository
) {

    private val log = LoggerFactory.getLogger(ImageService::class.java)

    /**
     * Save a image.
     *
     * @param image the entity to save.
     * @return the persisted entity.
     */
    fun save(image: Image): Image {
        log.debug("Request to save Image : {}", image)
        val result = imageRepository.save(image)
        imageSearchRepository.save(result)
        return result
    }

    /**
     * Get all the images.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Image> {
        log.debug("Request to get all Images")
        return imageRepository.findAll(pageable)
    }

    /**
     * Get one image by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Image> {
        log.debug("Request to get Image : {}", id)
        return imageRepository.findById(id)
    }

    /**
     * Delete the image by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Image : {}", id)

        imageRepository.deleteById(id)
        imageSearchRepository.deleteById(id)
    }

    /**
     * Search for the image corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<Image> {
        log.debug("Request to search for a page of Images for query {}", query)
        return imageSearchRepository.search(queryStringQuery(query), pageable)
    }
}
