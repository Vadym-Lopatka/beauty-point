package com.beautypoint.app.repository

import com.beautypoint.app.domain.Variant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Spring Data  repository for the [Variant] entity.
 */
@Repository
interface VariantRepository : JpaRepository<Variant, Long>, JpaSpecificationExecutor<Variant> {

    @Query(value = "select distinct variant from Variant variant left join fetch variant.executors",
        countQuery = "select count(distinct variant) from Variant variant")
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Variant>

    @Query(value = "select distinct variant from Variant variant left join fetch variant.executors")
    fun findAllWithEagerRelationships(): MutableList<Variant>

    @Query("select variant from Variant variant left join fetch variant.executors where variant.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Variant>
}
