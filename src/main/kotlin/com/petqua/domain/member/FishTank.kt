package com.petqua.domain.member

import com.petqua.domain.member.TankSize.NONE
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class FishTank(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val memberId: Long,

    @Embedded
    val name: TankName,

    @Column(nullable = false)
    val installationDate: LocalDate,

    @Column(nullable = false)
    val size: TankSize = NONE,
) {
}
