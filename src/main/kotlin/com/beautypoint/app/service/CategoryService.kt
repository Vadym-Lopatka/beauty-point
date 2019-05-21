package com.beautypoint.app.service

import com.beautypoint.app.domain.Category
import com.beautypoint.app.repository.CategoryRepository
import com.beautypoint.app.repository.search.CategorySearchRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service Implementation for managing [Category].
 */
@Service
@Transactional
class CategoryService(
    val categoryRepository: CategoryRepository,
    val categorySearchRepository: CategorySearchRepository
) {

    private val log = LoggerFactory.getLogger(CategoryService::class.java)

    /**
     * Save a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    fun save(category: Category): Category {
        log.debug("Request to save Category : {}", category)
        val result = categoryRepository.save(category)
        categorySearchRepository.save(result)
        return result
    }

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Category> {
        log.debug("Request to get all Categories")
        return categoryRepository.findAll(pageable)
    }

    /**
     * Get one category by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Category> {
        log.debug("Request to get Category : {}", id)
        return categoryRepository.findById(id)
    }

    /**
     * Delete the category by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Category : {}", id)

        categoryRepository.deleteById(id)
        categorySearchRepository.deleteById(id)
    }

    /**
     * Search for the category corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<Category> {
        log.debug("Request to search for a page of Categories for query {}", query)
        return categorySearchRepository.search(queryStringQuery(query), pageable)
    }
}
