package com.beautypoint.app.domain

import com.beautypoint.app.domain.enumeration.OrderStatusEnum
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * A Record.
 */
@Entity
@Table(name = "record")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "record")
class Record(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    var id: Long? = null,

    @get: NotNull
    @Column(name = "booking_time", nullable = false)
    var bookingTime: Instant? = null,

    @get: NotNull
    @Column(name = "duration", nullable = false)
    var duration: Int? = null,

    @get: NotNull
    @Column(name = "total_price", precision = 21, scale = 2, nullable = false)
    var totalPrice: BigDecimal? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    var orderStatus: OrderStatusEnum? = null,

    @Column(name = "jhi_comment")
    var comment: String? = null,

    @ManyToOne
    @JsonIgnoreProperties("records")
    var master: Master? = null,

    @ManyToOne
    @JsonIgnoreProperties("records")
    var variant: Variant? = null,

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "record_option",
        joinColumns = [JoinColumn(name = "record_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "option_id", referencedColumnName = "id")])
    var options: MutableSet<Option> = mutableSetOf(),

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("records")
    var user: User? = null,

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("records")
    var salon: Salon? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addOption(option: Option): Record {
        this.options.add(option)
        return this
    }

    fun removeOption(option: Option): Record {
        this.options.remove(option)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Record) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "Record{" +
            "id=$id" +
            ", bookingTime='$bookingTime'" +
            ", duration=$duration" +
            ", totalPrice=$totalPrice" +
            ", orderStatus='$orderStatus'" +
            ", comment='$comment'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
