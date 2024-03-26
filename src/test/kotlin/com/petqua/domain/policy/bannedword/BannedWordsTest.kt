package com.petqua.domain.policy.bannedword

import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.CONTAINING_BANNED_WORD_NAME
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BannedWordsTest : StringSpec({

    "금지 단어를 포함하는지 검증한다" {
        val bannedWords = BannedWords(
            listOf(
                BannedWord(word = "금지"),
                BannedWord(word = "단어")
            )
        )

        shouldNotThrow<MemberException> {
            bannedWords.validateContainingBannedWord("포함하지않는이름")
        }
    }

    "금지 단어를 포함하면 예외를 던진다" {
        val bannedWords = BannedWords(
            listOf(
                BannedWord(word = "금지"),
                BannedWord(word = "단어")
            )
        )

        shouldThrow<MemberException> {
            bannedWords.validateContainingBannedWord("포함(금지)하는이름")
        }.exceptionType() shouldBe CONTAINING_BANNED_WORD_NAME
    }
})
