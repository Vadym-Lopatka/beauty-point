package com.beautypoint.app.repository

import com.beautypoint.app.domain.TimeTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [TimeTable] entity.
 */
@SuppressWarnings("unused")
@Repository
interface TimeTableRepository : JpaRepository<TimeTable, Long>, JpaSpecificationExecutor<TimeTable>
