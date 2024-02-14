package com.petqua.domain.product

import com.petqua.common.domain.BaseEntity
import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.product.WishProductException
import com.petqua.exception.product.WishProductExceptionType.FORBIDDEN_WISH_PRODUCT
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class WishProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val productId: Long = 0L,

    @Column(nullable = false)
    val memberId: Long = 0L,
) : BaseEntity() {

    fun validateOwner(accessMemberId: Long) {
        throwExceptionWhen(accessMemberId != this.memberId) { WishProductException(FORBIDDEN_WISH_PRODUCT) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WishProduct

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
