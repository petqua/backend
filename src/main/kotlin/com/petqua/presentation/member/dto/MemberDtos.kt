package com.petqua.presentation.member.dto

import com.petqua.application.member.dto.MemberAddProfileCommand
import com.petqua.application.member.dto.MemberSignUpCommand
import com.petqua.application.member.dto.PetFishAddCommand
import com.petqua.application.member.dto.UpdateProfileCommand
import io.swagger.v3.oas.annotations.media.Schema
import java.time.YearMonth

data class MemberSignUpRequest(
    @Schema(
        description = "마케팅 정보 수신 동의 여부",
        example = "true"
    )
    val hasAgreedToMarketingNotification: Boolean,
) {

    fun toCommand(authCredentialsId: Long): MemberSignUpCommand {
        return MemberSignUpCommand(
            authCredentialsId = authCredentialsId,
            hasAgreedToMarketingNotification = hasAgreedToMarketingNotification,
        )
    }
}

data class MemberAddProfileRequest(
    @Schema(
        description = "수조 이름",
        example = "펫쿠아 어항"
    )
    val fishTankName: String,

    @Schema(
        description = "수조 설치일",
        example = "2024-03"
    )
    val installationDate: YearMonth,

    @Schema(
        description = "수조 크기",
        example = "TANK_1",
        allowableValues = ["TANK_1", "TANK_1_HALF", "TANK_2", "TANK_3", "TANK_4", "TANK_5", "NONE"]
    )
    val fishTankSize: String,

    @Schema(
        description = "물생활 경력",
        example = "1"
    )
    val fishLifeYear: Int,

    val petFishes: List<PetFishAddRequest>,
) {

    fun toCommand(memberId: Long): MemberAddProfileCommand {
        return MemberAddProfileCommand(
            memberId = memberId,
            fishTankName = fishTankName,
            installationDate = installationDate,
            fishTankSize = fishTankSize,
            fishLifeYear = fishLifeYear,
            petFishes = petFishes.map {
                PetFishAddCommand(
                    fishId = it.fishId,
                    sex = it.sex,
                    count = it.count,
                )
            }
        )
    }
}

data class PetFishAddRequest(
    @Schema(
        description = "어종 id",
        example = "1"
    )
    val fishId: Long,

    @Schema(
        description = "성별",
        example = "FEMALE",
        allowableValues = ["FEMALE", "MALE", "HERMAPHRODITE", "NONE"]
    )
    val sex: String,

    @Schema(
        description = "개수",
        example = "1"
    )
    val count: Int,
)

data class UpdateProfileRequest(
    @Schema(
        description = "닉네임",
        example = "펫쿠아"
    )
    val nickname: String,
) {

    fun toCommand(memberId: Long): UpdateProfileCommand {
        return UpdateProfileCommand(
            memberId = memberId,
            nickname = nickname,
        )
    }
}
