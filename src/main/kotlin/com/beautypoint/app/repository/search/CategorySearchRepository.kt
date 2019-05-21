package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.Category
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Category] entity.
 */
interface CategorySearchRepository : ElasticsearchRepository<Category, Long>
