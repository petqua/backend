package com.petqua.domain.product.option

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigDecimal

@Entity
class ProductOption(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    @Enumerated(STRING)
    val sex: Sex,

    @Column(nullable = false)
    val additionalPrice: BigDecimal,
) : BaseEntity() {

    fun hasDistinctSex(): Boolean {
        return sex != Sex.HERMAPHRODITE
    }
}
