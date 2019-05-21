package com.beautypoint.app.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of SalonSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class SalonSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockSalonSearchRepository: SalonSearchRepository
}
