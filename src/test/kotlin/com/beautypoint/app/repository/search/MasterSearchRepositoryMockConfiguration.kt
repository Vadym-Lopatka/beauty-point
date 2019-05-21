package com.beautypoint.app.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of MasterSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class MasterSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockMasterSearchRepository: MasterSearchRepository
}
