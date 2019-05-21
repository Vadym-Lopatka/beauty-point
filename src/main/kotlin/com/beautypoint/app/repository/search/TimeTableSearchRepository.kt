package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.TimeTable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [TimeTable] entity.
 */
interface TimeTableSearchRepository : ElasticsearchRepository<TimeTable, Long>
