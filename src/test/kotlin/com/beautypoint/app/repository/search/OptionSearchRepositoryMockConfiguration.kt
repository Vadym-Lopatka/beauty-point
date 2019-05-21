package com.beautypoint.app.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of OptionSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class OptionSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockOptionSearchRepository: OptionSearchRepository
}
