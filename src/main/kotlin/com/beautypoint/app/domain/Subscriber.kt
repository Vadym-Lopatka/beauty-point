package com.beautypoint.app.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

/**
 * A Subscriber.
 */
@Entity
@Table(name = "subscriber")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "subscriber")
class Subscriber(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    var id: Long? = null,

    @Column(name = "firs_name")
    var firsName: String? = null,

    @get: NotNull
    @get: Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Column(name = "email", nullable = false)
    var email: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Subscriber) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "Subscriber{" +
            "id=$id" +
            ", firsName='$firsName'" +
            ", email='$email'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
