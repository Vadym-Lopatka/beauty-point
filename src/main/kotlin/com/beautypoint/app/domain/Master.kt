package com.beautypoint.app.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * A Master.
 */
@Entity
@Table(name = "master")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "master")
class Master(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    var id: Long? = null,

    @ManyToOne
    @JsonIgnoreProperties("masters")
    var salon: Salon? = null,

    @OneToMany(mappedBy = "master")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var records: MutableSet<Record> = mutableSetOf(),

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "master_category",
        joinColumns = [JoinColumn(name = "master_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "category_id", referencedColumnName = "id")])
    var categories: MutableSet<Category> = mutableSetOf(),

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("images")
    var user: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addRecord(record: Record): Master {
        this.records.add(record)
        record.master = this
        return this
    }

    fun removeRecord(record: Record): Master {
        this.records.remove(record)
        record.master = null
        return this
    }

    fun addCategory(category: Category): Master {
        this.categories.add(category)
        return this
    }

    fun removeCategory(category: Category): Master {
        this.categories.remove(category)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Master) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "Master{" +
            "id=$id" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
