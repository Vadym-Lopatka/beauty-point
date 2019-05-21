package com.beautypoint.app.repository

import com.beautypoint.app.domain.Salon
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Spring Data  repository for the [Salon] entity.
 */
@Repository
interface SalonRepository : JpaRepository<Salon, Long>, JpaSpecificationExecutor<Salon> {

    @Query("select salon from Salon salon where salon.owner.login = ?#{principal.username}")
    fun findByOwnerIsCurrentUser(): MutableList<Salon>

    @Query(value = "select distinct salon from Salon salon left join fetch salon.categories",
        countQuery = "select count(distinct salon) from Salon salon")
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Salon>

    @Query(value = "select distinct salon from Salon salon left join fetch salon.categories")
    fun findAllWithEagerRelationships(): MutableList<Salon>

    @Query("select salon from Salon salon left join fetch salon.categories where salon.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Salon>
}
