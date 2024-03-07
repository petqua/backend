package com.petqua.domain.fish

import com.petqua.exception.fish.FishException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class SpeciesTest : StringSpec({

    "어종을 생성한다" {
        val name = "베타"

        shouldNotThrow<FishException> {
            Species.from(name)
        }
    }

    "어종을 생성할 때 이름을 입력하지 않으면 예외를 던진다" {
        val name = ""

        shouldThrow<FishException> {
            Species.from(name)
        }
    }
})
