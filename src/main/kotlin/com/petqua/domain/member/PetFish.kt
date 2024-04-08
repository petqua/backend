package com.petqua.domain.member

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class PetFish(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    val fishId: Long,

    @Column(nullable = false)
    val fishTankId: Long,

    @Column(nullable = false)
    @Enumerated(value = STRING)
    val sex: PetFishSex,

    @Embedded
    val count: PetFishCount,
) {
}
