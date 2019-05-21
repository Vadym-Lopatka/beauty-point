package com.beautypoint.app.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * A TimeTable.
 */
@Entity
@Table(name = "time_table")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "timetable")
class TimeTable(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    var id: Long? = null,

    @get: NotNull
    @Column(name = "every_day_equal", nullable = false)
    var everyDayEqual: Boolean? = null,

    @get: NotNull
    @Column(name = "mo", nullable = false)
    var mo: Long? = null,

    @get: NotNull
    @Column(name = "tu", nullable = false)
    var tu: Long? = null,

    @get: NotNull
    @Column(name = "we", nullable = false)
    var we: Long? = null,

    @get: NotNull
    @Column(name = "th", nullable = false)
    var th: Long? = null,

    @get: NotNull
    @Column(name = "fr", nullable = false)
    var fr: Long? = null,

    @get: NotNull
    @Column(name = "sa", nullable = false)
    var sa: Long? = null,

    @get: NotNull
    @Column(name = "su", nullable = false)
    var su: Long? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeTable) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "TimeTable{" +
            "id=$id" +
            ", everyDayEqual='$everyDayEqual'" +
            ", mo=$mo" +
            ", tu=$tu" +
            ", we=$we" +
            ", th=$th" +
            ", fr=$fr" +
            ", sa=$sa" +
            ", su=$su" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
