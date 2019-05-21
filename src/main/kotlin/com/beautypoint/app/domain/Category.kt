package com.beautypoint.app.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * A Category.
 */
@Entity
@Table(name = "category")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "category")
class Category(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    var id: Long? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @get: NotNull
    @Column(name = "main", nullable = false)
    var main: Boolean? = null,

    @ManyToOne
    @JsonIgnoreProperties("categories")
    var parent: Category? = null,

    @ManyToMany(mappedBy = "categories")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    var salons: MutableSet<Salon> = mutableSetOf(),

    @ManyToMany(mappedBy = "categories")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    var masters: MutableSet<Master> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addSalon(salon: Salon): Category {
        this.salons.add(salon)
        salon.categories.add(this)
        return this
    }

    fun removeSalon(salon: Salon): Category {
        this.salons.remove(salon)
        salon.categories.remove(this)
        return this
    }

    fun addMaster(master: Master): Category {
        this.masters.add(master)
        master.categories.add(this)
        return this
    }

    fun removeMaster(master: Master): Category {
        this.masters.remove(master)
        master.categories.remove(this)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Category) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "Category{" +
            "id=$id" +
            ", name='$name'" +
            ", main='$main'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
