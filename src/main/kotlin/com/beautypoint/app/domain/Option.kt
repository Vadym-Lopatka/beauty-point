package com.beautypoint.app.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * A Option.
 */
@Entity
@Table(name = "option")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "option")
class Option(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    var id: Long? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @get: NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    var price: BigDecimal? = null,

    @get: NotNull
    @Column(name = "session_time", nullable = false)
    var sessionTime: Int? = null,

    @get: NotNull
    @Column(name = "active", nullable = false)
    var active: Boolean? = null,

    @ManyToOne
    @JsonIgnoreProperties("options")
    var offer: Offer? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Option) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "Option{" +
            "id=$id" +
            ", name='$name'" +
            ", price=$price" +
            ", sessionTime=$sessionTime" +
            ", active='$active'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
