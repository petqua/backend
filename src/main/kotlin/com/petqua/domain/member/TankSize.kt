package com.petqua.domain.member

import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_TANK_SIZE
import java.util.Locale.ENGLISH

enum class TankSize(
    val description: String,
) {

    TANK_1("1자어항(~30CM)"),
    TANK_1_HALF("1.5자어항(~45CM)"),
    TANK_2("2자어항(~66CM)"),
    TANK_3("3자어항(~100CM)"),
    TANK_4("4자어항(~130CM)"),
    TANK_5("5자어항(150CM~)"),
    NONE("잘 모르겠어요"),
    ;

    companion object {
        fun from(name: String): TankSize {
            return enumValues<TankSize>().find { it.name == name.uppercase(ENGLISH) }
                ?: throw MemberException(INVALID_MEMBER_FISH_TANK_SIZE)
        }
    }
}

