package com.beautypoint.app.repository

import com.beautypoint.app.domain.Subscriber
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Subscriber] entity.
 */
@SuppressWarnings("unused")
@Repository
interface SubscriberRepository : JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber>
