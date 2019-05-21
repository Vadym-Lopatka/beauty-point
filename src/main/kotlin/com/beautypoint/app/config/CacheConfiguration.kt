package com.beautypoint.app.config

import io.github.jhipster.config.JHipsterProperties
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.jsr107.Eh107Configuration
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfiguration(jHipsterProperties: JHipsterProperties) {

    private val jcacheConfiguration: javax.cache.configuration.Configuration<Any, Any>

    init {
        val ehcache = jHipsterProperties.cache.ehcache

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Any::class.java, Any::class.java,
                ResourcePoolsBuilder.heap(ehcache.maxEntries))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.timeToLiveSeconds.toLong())))
                .build())
    }

    @Bean
    fun cacheManagerCustomizer(): JCacheManagerCustomizer {
        return JCacheManagerCustomizer { cm ->
            createCache(cm, com.beautypoint.app.repository.UserRepository.USERS_BY_LOGIN_CACHE)
            createCache(cm, com.beautypoint.app.repository.UserRepository.USERS_BY_EMAIL_CACHE)
            createCache(cm, com.beautypoint.app.domain.User::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Authority::class.java.name)
            createCache(cm, com.beautypoint.app.domain.User::class.java.name + ".authorities")
            createCache(cm, com.beautypoint.app.domain.Category::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Category::class.java.name + ".salons")
            createCache(cm, com.beautypoint.app.domain.Category::class.java.name + ".masters")
            createCache(cm, com.beautypoint.app.domain.Image::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Master::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Master::class.java.name + ".records")
            createCache(cm, com.beautypoint.app.domain.Master::class.java.name + ".categories")
            createCache(cm, com.beautypoint.app.domain.Offer::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Offer::class.java.name + ".variants")
            createCache(cm, com.beautypoint.app.domain.Offer::class.java.name + ".options")
            createCache(cm, com.beautypoint.app.domain.Option::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Record::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Record::class.java.name + ".options")
            createCache(cm, com.beautypoint.app.domain.Salon::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Salon::class.java.name + ".offers")
            createCache(cm, com.beautypoint.app.domain.Salon::class.java.name + ".masters")
            createCache(cm, com.beautypoint.app.domain.Salon::class.java.name + ".categories")
            createCache(cm, com.beautypoint.app.domain.Subscriber::class.java.name)
            createCache(cm, com.beautypoint.app.domain.TimeTable::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Variant::class.java.name)
            createCache(cm, com.beautypoint.app.domain.Variant::class.java.name + ".executors")
            // jhipster-needle-ehcache-add-entry
        }
    }

    private fun createCache(cm: javax.cache.CacheManager, cacheName: String) {
        val cache: javax.cache.Cache<Any, Any>? = cm.getCache(cacheName)
        if (cache != null) {
            cm.destroyCache(cacheName)
        }
        cm.createCache(cacheName, jcacheConfiguration)
    }
}
