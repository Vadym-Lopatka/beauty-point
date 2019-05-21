package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.Option
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Option] entity.
 */
interface OptionSearchRepository : ElasticsearchRepository<Option, Long>
