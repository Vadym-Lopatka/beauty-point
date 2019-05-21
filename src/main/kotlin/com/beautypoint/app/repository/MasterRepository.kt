package com.beautypoint.app.repository

import com.beautypoint.app.domain.Master
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Spring Data  repository for the [Master] entity.
 */
@Repository
interface MasterRepository : JpaRepository<Master, Long>, JpaSpecificationExecutor<Master> {

    @Query("select master from Master master where master.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Master>

    @Query(value = "select distinct master from Master master left join fetch master.categories",
        countQuery = "select count(distinct master) from Master master")
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Master>

    @Query(value = "select distinct master from Master master left join fetch master.categories")
    fun findAllWithEagerRelationships(): MutableList<Master>

    @Query("select master from Master master left join fetch master.categories where master.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Master>
}
