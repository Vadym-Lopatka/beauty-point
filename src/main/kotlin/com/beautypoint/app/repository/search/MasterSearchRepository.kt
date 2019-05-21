package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.Master
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Master] entity.
 */
interface MasterSearchRepository : ElasticsearchRepository<Master, Long>
