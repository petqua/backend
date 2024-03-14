package com.petqua.test.fixture

import com.petqua.domain.member.PetFish
import com.petqua.domain.member.PetFishCount
import com.petqua.domain.member.PetFishSex
import com.petqua.domain.member.PetFishSex.NONE

fun petFish(
    id: Long = 0L,
    memberId: Long = 0L,
    fishId: Long = 0L,
    fishTankId: Long = 0L,
    sex: PetFishSex = NONE,
    count: Int = 1,
): PetFish {
    return PetFish(
        id = id,
        memberId = memberId,
        fishId = fishId,
        fishTankId = fishTankId,
        sex = sex,
        count = PetFishCount(count),
    )
}
