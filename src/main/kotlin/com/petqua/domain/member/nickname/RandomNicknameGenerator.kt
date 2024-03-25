package com.petqua.domain.member.nickname

import com.petqua.domain.member.nickname.Nickname.Companion.MAX_NICKNAME_LENGTH
import org.springframework.stereotype.Component
import kotlin.math.pow

private const val MIN_NUMBER = 1
private const val RANDOM_WORD_COUNT = 2
private const val SEPARATOR = " "

@Component
class RandomNicknameGenerator() : NicknameGenerator {

    override fun generate(nicknameWords: List<NicknameWord>): Nickname {
        val selectedWords = nicknameWords.shuffled().take(RANDOM_WORD_COUNT).map { it.word }
        val wordsNickname = selectedWords.joinToString(SEPARATOR)
        val wordsLength = wordsNickname.length

        val maxNumberLength = MAX_NICKNAME_LENGTH - wordsLength
        if (maxNumberLength > 0) {
            return Nickname.from(wordsNickname)
        }

        val maxNumber = (10.toDouble().pow(maxNumberLength) - 1).toInt() // 10^n - 1
        val randomNumber = (MIN_NUMBER..maxNumber).random()
        return Nickname.from("${wordsNickname}$randomNumber")
    }
}
