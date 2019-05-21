package com.beautypoint.app.repository

import com.beautypoint.app.domain.Option
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Option] entity.
 */
@SuppressWarnings("unused")
@Repository
interface OptionRepository : JpaRepository<Option, Long>, JpaSpecificationExecutor<Option>
