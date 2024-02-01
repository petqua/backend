package com.petqua.domain.wish

import com.petqua.common.domain.BaseEntity
import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.wish.WishException
import com.petqua.exception.wish.WishExceptionType.FORBIDDEN_WISH
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Wish(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val productId: Long = 0L,

    @Column(nullable = false)
    val memberId: Long = 0L,
) : BaseEntity() {

    fun validateOwner(accessMemberId: Long) {
        throwExceptionWhen(accessMemberId != this.memberId) { WishException(FORBIDDEN_WISH) }
    }
}
