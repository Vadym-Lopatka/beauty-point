package com.beautypoint.app.domain

import com.beautypoint.app.domain.enumeration.ImageTypeEnum
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * A Image.
 */
@Entity
@Table(name = "image")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "image")
class Image(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    var id: Long? = null,

    @get: NotNull
    @Column(name = "source", nullable = false)
    var source: String? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    var imageType: ImageTypeEnum? = null,

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("images")
    var owner: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Image) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "Image{" +
            "id=$id" +
            ", source='$source'" +
            ", imageType='$imageType'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
