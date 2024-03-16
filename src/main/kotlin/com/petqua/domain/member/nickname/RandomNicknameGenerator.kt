package com.petqua.domain.member.nickname

import org.springframework.stereotype.Component

private const val START_NUMBER = 1
private const val END_NUMBER = 9999
private const val RANDOM_WORD_COUNT = 2
private const val SEPARATOR = " "

@Component
class RandomNicknameGenerator() : NicknameGenerator {

    override fun generate(nicknameWords: List<NicknameWord>): Nickname {
        // 정책 세부정보 문의 및 반영
        // 숫자는 몇 자리?
        val selectedWords = nicknameWords.shuffled().take(RANDOM_WORD_COUNT).map { it.value }
        val wordsNickname = selectedWords.joinToString(SEPARATOR)
        val randomNumber = (START_NUMBER..END_NUMBER).random()
        return Nickname.from("${wordsNickname}$randomNumber")
    }
}
