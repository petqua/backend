package com.petqua.domain.member

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
}

