package com.petqua.domain.member

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_TANK_NAME
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.util.regex.Pattern

private const val WHITESPACE = " "

@Embeddable
data class TankName(
    @Column(nullable = false, name = "name")
    private val value: String,
) {

    init {
        validateEdgeWhitespace()
        validateConsecutiveWhitespaces()
        validateCharactersAndLength()
    }

    private fun validateEdgeWhitespace() {
        throwExceptionWhen(value.startsWith(WHITESPACE) || value.endsWith(WHITESPACE)) {
            MemberException(INVALID_MEMBER_FISH_TANK_NAME)
        }
    }

    private fun validateConsecutiveWhitespaces() {
        throwExceptionWhen(whitespacePattern.toRegex().containsMatchIn(value)) {
            MemberException(INVALID_MEMBER_FISH_TANK_NAME)
        }
    }

    private fun validateCharactersAndLength() {
        throwExceptionWhen(!wordPattern.matcher(value).matches()) {
            MemberException(INVALID_MEMBER_FISH_TANK_NAME)
        }
    }

    companion object {
        private val wordPattern = Pattern.compile("^[a-zA-Z0-9가-힣,/_ ]{2,18}\$")
        private val whitespacePattern = Pattern.compile("$WHITESPACE{2,}")
    }
}
