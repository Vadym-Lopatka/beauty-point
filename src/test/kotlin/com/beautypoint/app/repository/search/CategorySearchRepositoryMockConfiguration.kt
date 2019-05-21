package com.beautypoint.app.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of CategorySearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class CategorySearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockCategorySearchRepository: CategorySearchRepository
}
