package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.Image
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Image] entity.
 */
interface ImageSearchRepository : ElasticsearchRepository<Image, Long>
