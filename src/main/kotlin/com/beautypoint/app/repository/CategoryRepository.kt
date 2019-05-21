package com.beautypoint.app.repository

import com.beautypoint.app.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Category] entity.
 */
@SuppressWarnings("unused")
@Repository
interface CategoryRepository : JpaRepository<Category, Long>, JpaSpecificationExecutor<Category>
