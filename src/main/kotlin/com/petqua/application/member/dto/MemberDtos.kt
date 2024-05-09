package com.petqua.application.member.dto

import com.petqua.domain.member.FishTank
import com.petqua.domain.member.PetFish
import com.petqua.domain.member.PetFishCount
import com.petqua.domain.member.PetFishSex
import com.petqua.domain.member.PetFishes
import java.time.YearMonth

data class MemberSignUpCommand(
    val authCredentialsId: Long,
    val hasAgreedToMarketingNotification: Boolean,
)

data class MemberAddProfileCommand(
    val memberId: Long,
    val fishTankName: String,
    val installationDate: YearMonth,
    val fishTankSize: String,
    val fishLifeYear: Int,
    val petFishes: List<PetFishAddCommand>,
) {

    fun toFishTank(memberId: Long): FishTank {
        return FishTank.of(
            memberId = memberId,
            fishTankName = fishTankName,
            installationDate = installationDate,
            fishTankSize = fishTankSize,
        )
    }

    fun toPetFishes(memberId: Long, fishTankId: Long): PetFishes {
        return PetFishes(petFishes.map { it.toPetFish(memberId, fishTankId) })

    }
}

data class PetFishAddCommand(
    val fishId: Long,
    val sex: String,
    val count: Int,
) {
    fun toPetFish(memberId: Long, fishTankId: Long): PetFish {
        return PetFish(
            memberId = memberId,
            fishId = fishId,
            fishTankId = fishTankId,
            sex = PetFishSex.from(sex),
            count = PetFishCount(count),
        )
    }
}

data class UpdateProfileCommand(
    val memberId: Long,
    val nickname: String
)
