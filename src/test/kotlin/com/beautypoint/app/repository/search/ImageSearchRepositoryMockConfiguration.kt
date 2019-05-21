package com.beautypoint.app.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of ImageSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class ImageSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockImageSearchRepository: ImageSearchRepository
}
