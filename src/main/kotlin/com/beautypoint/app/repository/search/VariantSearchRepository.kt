package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.Variant
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Variant] entity.
 */
interface VariantSearchRepository : ElasticsearchRepository<Variant, Long>
