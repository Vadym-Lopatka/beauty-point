package com.beautypoint.app.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of TimeTableSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class TimeTableSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockTimeTableSearchRepository: TimeTableSearchRepository
}
