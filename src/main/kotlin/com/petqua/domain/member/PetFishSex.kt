package com.petqua.domain.member

import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_SEX
import java.util.Locale.ENGLISH

enum class PetFishSex(
    val description: String,
) {

    FEMALE("암"),
    MALE("수"),
    HERMAPHRODITE("자웅동체"),
    NONE("성별을 모르겠어요")
    ;

    companion object {
        fun from(name: String): PetFishSex {
            return enumValues<PetFishSex>().find { it.name == name.uppercase(ENGLISH) }
                ?: throw MemberException(INVALID_MEMBER_FISH_SEX)
        }
    }
}
