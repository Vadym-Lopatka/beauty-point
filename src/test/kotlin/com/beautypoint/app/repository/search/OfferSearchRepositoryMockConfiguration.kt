package com.beautypoint.app.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of OfferSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class OfferSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockOfferSearchRepository: OfferSearchRepository
}
