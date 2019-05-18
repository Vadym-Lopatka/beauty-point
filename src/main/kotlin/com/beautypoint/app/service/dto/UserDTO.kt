package com.beautypoint.app.service.dto

import com.beautypoint.app.config.Constants
import com.beautypoint.app.domain.User

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

import java.time.Instant

/**
 * A DTO representing a user, with his authorities.
 */
open class UserDTO(
    var id: Long? = null,

    @field:NotBlank
    @field:Pattern(regexp = Constants.LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    var login: String? = null,

    @field:Size(max = 50)
    var firstName: String? = null,

    @field:Size(max = 50)
    var lastName: String? = null,

    @field:Email
    @field:Size(min = 5, max = 254)
    var email: String? = null,

    @field:Size(max = 256)
    var imageUrl: String? = null,

    var activated: Boolean = false,

    @field:Size(min = 2, max = 6)
    var langKey: String? = null,

    var createdBy: String? = null,

    var createdDate: Instant? = null,

    var lastModifiedBy: String? = null,

    var lastModifiedDate: Instant? = null,

    var authorities: Set<String>? = null

) {
    constructor(user: User) :
        this(
            user.id, user.login, user.firstName, user.lastName, user.email,
            user.imageUrl, user.activated, user.langKey,
            user.createdBy, user.createdDate, user.lastModifiedBy, user.lastModifiedDate,
            user.authorities.map { it.name }.filterNotNullTo(mutableSetOf())
        )

    fun isActivated(): Boolean = activated

    override fun toString(): String {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", activated=" + activated +
            ", langKey='" + langKey + '\'' +
            ", createdBy=" + createdBy +
            ", createdDate=" + createdDate +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            ", lastModifiedDate=" + lastModifiedDate +
            ", authorities=" + authorities +
            "}"
    }
}
