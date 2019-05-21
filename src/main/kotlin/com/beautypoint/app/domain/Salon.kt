package com.beautypoint.app.domain

import com.beautypoint.app.domain.enumeration.SalonStatusEnum
import com.beautypoint.app.domain.enumeration.SalonTypeEnum
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * A Salon.
 */
@Entity
@Table(name = "salon")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "salon")
class Salon(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    var id: Long? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "slogan")
    var slogan: String? = null,

    @get: NotNull
    @Column(name = "location", nullable = false)
    var location: String? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: SalonStatusEnum? = null,

    @Column(name = "system_comment")
    var systemComment: String? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_type", nullable = false)
    var type: SalonTypeEnum? = null,

    @OneToOne
    @JoinColumn(unique = true)
    var image: Image? = null,

    @OneToOne
    @JoinColumn(unique = true)
    var timeTable: TimeTable? = null,

    @OneToMany(mappedBy = "salon")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var offers: MutableSet<Offer> = mutableSetOf(),

    @OneToMany(mappedBy = "salon")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var masters: MutableSet<Master> = mutableSetOf(),

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "salon_category",
        joinColumns = [JoinColumn(name = "salon_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "category_id", referencedColumnName = "id")])
    var categories: MutableSet<Category> = mutableSetOf(),

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("images")
    var owner: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {

    fun addOffer(offer: Offer): Salon {
        this.offers.add(offer)
        offer.salon = this
        return this
    }

    fun removeOffer(offer: Offer): Salon {
        this.offers.remove(offer)
        offer.salon = null
        return this
    }

    fun addMaster(master: Master): Salon {
        this.masters.add(master)
        master.salon = this
        return this
    }

    fun removeMaster(master: Master): Salon {
        this.masters.remove(master)
        master.salon = null
        return this
    }

    fun addCategory(category: Category): Salon {
        this.categories.add(category)
        return this
    }

    fun removeCategory(category: Category): Salon {
        this.categories.remove(category)
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Salon) return false
        if (other.id == null || id == null) return false

        return Objects.equals(id, other.id)
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString(): String {
        return "Salon{" +
            "id=$id" +
            ", name='$name'" +
            ", slogan='$slogan'" +
            ", location='$location'" +
            ", status='$status'" +
            ", systemComment='$systemComment'" +
            ", type='$type'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
