package com.petqua.domain.member.nickname

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_NICKNAME
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.util.regex.Pattern

private const val WHITESPACE = " "
private const val EMPTY = ""

@Embeddable
class Nickname private constructor(
    @Column(nullable = false, name = "nickname")
    val value: String,
) {
    fun isEmpty(): Boolean {
        return this == EMPTY_NICKNAME
    }

    companion object {
        private val wordPattern = Pattern.compile("^[a-zA-Z0-9가-힣 ]{2,12}\$")
        private val whitespacePattern = Pattern.compile("$WHITESPACE{2,}")
        private val EMPTY_NICKNAME = Nickname(EMPTY)

        fun emptyNickname(): Nickname {
            return EMPTY_NICKNAME
        }

        fun from(value: String): Nickname {
            validateEdgeWhitespace(value)
            validateConsecutiveWhitespaces(value)
            validateCharactersAndLength(value)
            return Nickname(value)
        }


        private fun validateEdgeWhitespace(value: String) {
            throwExceptionWhen(value.startsWith(WHITESPACE) || value.endsWith(WHITESPACE)) {
                MemberException(INVALID_MEMBER_NICKNAME)
            }
        }

        private fun validateConsecutiveWhitespaces(value: String) {
            throwExceptionWhen(whitespacePattern.toRegex().containsMatchIn(value)) {
                MemberException(INVALID_MEMBER_NICKNAME)
            }
        }

        private fun validateCharactersAndLength(value: String) {
            throwExceptionWhen(!wordPattern.matcher(value).matches()) {
                MemberException(INVALID_MEMBER_NICKNAME)
            }
        }
    }
}
