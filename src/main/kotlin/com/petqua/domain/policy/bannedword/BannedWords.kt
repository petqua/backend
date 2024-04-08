package com.petqua.domain.policy.bannedword

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.CONTAINING_BANNED_WORD_NAME


class BannedWords(
    private val values: List<BannedWord>,
) {

    fun validateContainingBannedWord(name: String) {
        for (bannedWord in values) {
            throwExceptionWhen(name.uppercase().contains(bannedWord.word.uppercase())) {
                MemberException(CONTAINING_BANNED_WORD_NAME)
            }
        }
    }
}
