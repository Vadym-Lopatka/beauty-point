package com.beautypoint.app.domain

import com.beautypoint.app.domain.enumeration.OfferStatusEnum
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
 * A Offer.
 */
@Entity
@Table(name = "offer")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "offer")
class Offer(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    var id: Long? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @get: NotNull
    @Column(name = "description", nullable = false)
    var description: String? = null,

    @get: NotNull
    @Column(name = "price_low", precision = 21, scale = 2, nullable = false)
    var priceLow: BigDecimal? = null,

    @get: NotNull
    @Column(name = "price_high", precision = 21, scale = 2, nullable = false)
    var priceHigh: BigDecimal? = null,

    @get: NotNull
    @Column(name = "active", nullable = false)
    var active: Boolean? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OfferStatusEnum? = null,

    @ManyToOne
    @JsonIgnoreProperties("offers")
    var salon: Salon? = null,

    @ManyToOne
    @JsonIgnoreProperties("offers")
    var image: Image? = null,

    @OneToMany(mappedBy = "offer")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var variants: MutableSet<Variant> = mutableSetOf(),

    @OneToMany(mappedBy = "offer")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var options: MutableSet<Option> = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties("offers")
    var category: Category? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addVariant(variant: Variant): Offer {
        this.variants.add(variant)
        variant.offer = this
        return this
    }

    fun removeVariant(variant: Variant): Offer {
        this.variants.remove(variant)
        variant.offer = null
        return this
    }

    fun addOption(option: Option): Offer {
        this.options.add(option)
        option.offer = this
        return this
    }

    fun removeOption(option: Option): Offer {
        this.options.remove(option)
        option.offer = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Offer) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "Offer{" +
            "id=$id" +
            ", name='$name'" +
            ", description='$description'" +
            ", priceLow=$priceLow" +
            ", priceHigh=$priceHigh" +
            ", active='$active'" +
            ", status='$status'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
