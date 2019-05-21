package com.beautypoint.app.repository

import com.beautypoint.app.domain.Record
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Spring Data  repository for the [Record] entity.
 */
@Repository
interface RecordRepository : JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {

    @Query("select record from Record record where record.user.login = ?#{principal.username}")
    fun findByUserIsCurrentUser(): MutableList<Record>

    @Query(value = "select distinct record from Record record left join fetch record.options",
        countQuery = "select count(distinct record) from Record record")
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Record>

    @Query(value = "select distinct record from Record record left join fetch record.options")
    fun findAllWithEagerRelationships(): MutableList<Record>

    @Query("select record from Record record left join fetch record.options where record.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Record>
}
