package com.petqua.domain.product.option

import com.petqua.common.domain.BaseEntity
import com.petqua.common.domain.Money
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class ProductOption(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    @Enumerated(STRING)
    val sex: Sex,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "additional_price", nullable = false))
    val additionalPrice: Money,
) : BaseEntity() {

    fun hasDistinctSex(): Boolean {
        return sex != Sex.HERMAPHRODITE
    }

    fun isSame(other: ProductOption): Boolean {
        return productId == other.productId
                && sex == other.sex
                && additionalPrice == other.additionalPrice
    }

}
