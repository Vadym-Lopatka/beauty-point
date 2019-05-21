package com.beautypoint.app.repository.search

import com.beautypoint.app.domain.Offer
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * Spring Data Elasticsearch repository for the [Offer] entity.
 */
interface OfferSearchRepository : ElasticsearchRepository<Offer, Long>
