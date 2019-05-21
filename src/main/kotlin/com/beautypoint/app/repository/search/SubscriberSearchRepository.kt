package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.Subscriber
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Subscriber] entity.
 */
interface SubscriberSearchRepository : ElasticsearchRepository<Subscriber, Long>
