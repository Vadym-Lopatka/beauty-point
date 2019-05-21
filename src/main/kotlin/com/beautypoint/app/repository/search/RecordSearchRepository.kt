package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.Record
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Record] entity.
 */
interface RecordSearchRepository : ElasticsearchRepository<Record, Long>
