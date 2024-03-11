package com.petqua.domain.member

enum class PetFishSex(
    val description: String,
) {

    FEMALE("암"),
    MALE("수"),
    HERMAPHRODITE("자웅동체"),
    NONE("성별을 모르겠어요")
    ;
}
