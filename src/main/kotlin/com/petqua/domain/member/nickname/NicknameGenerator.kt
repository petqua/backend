package com.petqua.domain.member.nickname

interface NicknameGenerator {

    fun generate(nicknameWords: List<NicknameWord>): Nickname

    fun attemptCount(): Int
}
