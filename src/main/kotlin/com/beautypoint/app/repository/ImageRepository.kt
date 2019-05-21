package com.beautypoint.app.repository

import com.beautypoint.app.domain.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Image] entity.
 */
@SuppressWarnings("unused")
@Repository
interface ImageRepository : JpaRepository<Image, Long>, JpaSpecificationExecutor<Image> {

    @Query("select image from Image image where image.owner.login = ?#{principal.username}")
    fun findByOwnerIsCurrentUser(): MutableList<Image>
}
