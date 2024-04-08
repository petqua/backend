package com.petqua.test.fixture

import com.petqua.application.member.dto.MemberAddProfileCommand
import com.petqua.application.member.dto.PetFishAddCommand
import com.petqua.domain.auth.Authority
import com.petqua.domain.member.FishLifeYear
import com.petqua.domain.member.Member
import com.petqua.domain.member.nickname.Nickname
import com.petqua.presentation.member.dto.MemberAddProfileRequest
import com.petqua.presentation.member.dto.PetFishAddRequest
import java.time.YearMonth

fun member(
    id: Long = 0L,
    authCredentialsId: Long = 0L,
    authority: Authority = Authority.MEMBER,
    nickname: String = "nickname",
    profileImageUrl: String = "imageUrl",
    fishTankCount: Int = 1,
    fishLifeYear: Int = 1,
    hasAgreedToMarketingNotification: Boolean = false,
    isDeleted: Boolean = false,
): Member {
    return Member(
        id = id,
        authCredentialsId = authCredentialsId,
        authority = authority,
        nickname = Nickname.from(nickname),
        profileImageUrl = profileImageUrl,
        fishTankCount = fishTankCount,
        fishLifeYear = FishLifeYear.from(fishLifeYear),
        hasAgreedToMarketingNotification = hasAgreedToMarketingNotification,
        isDeleted = isDeleted,
    )
}

fun memberAddProfileCommand(
    memberId: Long,
    fishTankName: String = "펫쿠아 어항",
    installationDate: YearMonth = YearMonth.of(2024, 3),
    fishTankSize: String = "TANK_1",
    fishLifeYear: Int = 1,
    petFishes: List<PetFishAddCommand> = listOf(
        PetFishAddCommand(
            fishId = 1L,
            sex = "FEMALE",
            count = 1
        )
    ),
): MemberAddProfileCommand {
    return MemberAddProfileCommand(
        memberId = memberId,
        fishTankName = fishTankName,
        installationDate = installationDate,
        fishTankSize = fishTankSize,
        fishLifeYear = fishLifeYear,
        petFishes = petFishes,
    )
}

fun memberAddProfileRequest(
    fishTankName: String = "펫쿠아 어항",
    installationDate: YearMonth = YearMonth.of(2024, 3),
    fishTankSize: String = "TANK_1",
    fishLifeYear: Int = 1,
    petFishes: List<PetFishAddRequest> = listOf(
        PetFishAddRequest(
            fishId = 1L,
            sex = "FEMALE",
            count = 1
        )
    ),
): MemberAddProfileRequest {
    return MemberAddProfileRequest(
        fishTankName = fishTankName,
        installationDate = installationDate,
        fishTankSize = fishTankSize,
        fishLifeYear = fishLifeYear,
        petFishes = petFishes,
    )
}
