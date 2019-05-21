package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.Salon
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Salon] entity.
 */
interface SalonSearchRepository : ElasticsearchRepository<Salon, Long>
