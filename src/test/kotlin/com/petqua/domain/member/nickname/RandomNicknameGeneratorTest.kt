package com.petqua.domain.member.nickname

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain

class RandomNicknameGeneratorTest : StringSpec({

    "닉네임을 생성한다" {
        val nicknameGenerator = RandomNicknameGenerator()

        val nickname = nicknameGenerator.generate(
            listOf(NicknameWord(word = "펫쿠아"), NicknameWord(word = "물고기"))
        )

        assertSoftly(nickname) {
            value shouldContain "펫쿠아"
            value shouldContain "물고기"
        }
    }
})
