package com.beautypoint.app.service

import com.beautypoint.app.domain.Category
import com.beautypoint.app.domain.Category_
import com.beautypoint.app.domain.Master_
import com.beautypoint.app.domain.Salon_
import com.beautypoint.app.repository.CategoryRepository
import com.beautypoint.app.repository.search.CategorySearchRepository
import com.beautypoint.app.service.dto.CategoryCriteria
import io.github.jhipster.service.QueryService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.criteria.JoinType

/**
 * Service for executing complex queries for [Category] entities in the database.
 * The main input is a [CategoryCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [Category] or a [Page] of [Category] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class CategoryQueryService(
    val categoryRepository: CategoryRepository,
    val categorySearchRepository: CategorySearchRepository
) : QueryService<Category>() {

    private val log = LoggerFactory.getLogger(CategoryQueryService::class.java)

    /**
     * Return a [MutableList] of [Category] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CategoryCriteria?): MutableList<Category> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return categoryRepository.findAll(specification)
    }

    /**
     * Return a [Page] of [Category] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: CategoryCriteria?, page: Pageable): Page<Category> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return categoryRepository.findAll(specification, page)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: CategoryCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return categoryRepository.count(specification)
    }

    /**
     * Function to convert [CategoryCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: CategoryCriteria?): Specification<Category?> {
        var specification: Specification<Category?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, Category_.id))
            }
            if (criteria.name != null) {
                specification = specification.and(buildStringSpecification(criteria.name, Category_.name))
            }
            if (criteria.main != null) {
                specification = specification.and(buildSpecification(criteria.main, Category_.main))
            }
            if (criteria.parentId != null) {
                specification = specification.and(buildSpecification(criteria.parentId) {
                    it.join(Category_.parent, JoinType.LEFT).get(Category_.id)
                })
            }
            if (criteria.salonId != null) {
                specification = specification.and(buildSpecification(criteria.salonId) {
                    it.join(Category_.salons, JoinType.LEFT).get(Salon_.id)
                })
            }
            if (criteria.masterId != null) {
                specification = specification.and(buildSpecification(criteria.masterId) {
                    it.join(Category_.masters, JoinType.LEFT).get(Master_.id)
                })
            }
        }
        return specification
    }
}
