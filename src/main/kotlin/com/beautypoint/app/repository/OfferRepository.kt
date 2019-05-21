package com.beautypoint.app.repository

import com.beautypoint.app.domain.Offer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Offer] entity.
 */
@SuppressWarnings("unused")
@Repository
interface OfferRepository : JpaRepository<Offer, Long>, JpaSpecificationExecutor<Offer>
