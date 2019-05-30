package com.beautypoint.app.web.rest.vm

import com.beautypoint.app.service.dto.UserDTO
import javax.validation.constraints.Size

/**
 * View Model extending the [UserDTO], which is meant to be used in the user management UI.
 */
class ManagedUserVM : UserDTO() {

    @field:Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    var password: String? = null

    override fun toString(): String {
        return "ManagedUserVM{" +
            "} " + super.toString()
    }

    companion object {
        const val PASSWORD_MIN_LENGTH: Int = 4
        const val PASSWORD_MAX_LENGTH: Int = 100
    }
}
