package com.beautypoint.app.config

import org.springframework.context.annotation.Configuration

import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaRepositories("com.beautypoint.app.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
@EnableElasticsearchRepositories("com.beautypoint.app.repository.search")
class DatabaseConfiguration
