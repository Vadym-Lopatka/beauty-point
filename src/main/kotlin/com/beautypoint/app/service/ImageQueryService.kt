package com.beautypoint.app.service

import com.beautypoint.app.domain.Image
import com.beautypoint.app.domain.Image_
import com.beautypoint.app.domain.User_
import com.beautypoint.app.repository.ImageRepository
import com.beautypoint.app.repository.search.ImageSearchRepository
import com.beautypoint.app.service.dto.ImageCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [Image] entities in the database.
 * The main input is a [ImageCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Image] or a [Page] of [Image] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class ImageQueryService(
    val imageRepository: ImageRepository,
    val imageSearchRepository: ImageSearchRepository
) : QueryService<Image>() {

    private val log = LoggerFactory.getLogger(ImageQueryService::class.java)

    /**
     * Return a [MutableList] of [Image] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ImageCriteria?): MutableList<Image> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return imageRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Image] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: ImageCriteria?, page: Pageable): Page<Image> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return imageRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: ImageCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return imageRepository.count(specification)
    }

    /**
     * Function to convert [ImageCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: ImageCriteria?): Specification<Image?> {
        var specification: Specification<Image?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, Image_.id))
            }
            if (criteria.source != null) {
                specification = specification.and(buildStringSpecification(criteria.source, Image_.source))
            }
            if (criteria.imageType != null) {
                specification = specification.and(buildSpecification(criteria.imageType, Image_.imageType))
            }
            if (criteria.ownerId != null) {
                specification = specification.and(buildSpecification(criteria.ownerId) {
                    it.join(Image_.owner, JoinType.LEFT).get(User_.id)
                })
            }
        }
        return specification
    }
}
