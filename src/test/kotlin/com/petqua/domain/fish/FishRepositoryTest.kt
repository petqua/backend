package com.petqua.domain.fish

import com.petqua.test.DataCleaner
import com.petqua.test.fixture.fish
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class FishRepositoryTest(
    private val fishRepository: FishRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("어종 id 목록을 입력해 존재하는 어종의 개수를 셀 때") {
        val fishA = fishRepository.save(fish(species = "fishA"))
        val fishB = fishRepository.save(fish(species = "fishB"))

        When("존재하는 id 목록을 입력하면") {
            val countsByIds = fishRepository.countsByIds(setOf(fishA.id, fishB.id))

            Then("개수를 반환한다") {
                countsByIds shouldBe 2
            }
        }

        When("id를 중복해서 입력하면") {
            val countsByIds = fishRepository.countsByIds(setOf(fishA.id, fishA.id))

            Then("중복된 id에 대한 개수는 세지 않는다") {
                countsByIds shouldBe 1
            }
        }

        When("존재하지 않는 id를 입력하면") {
            val countsByIds = fishRepository.countsByIds(setOf(Long.MIN_VALUE))

            Then("개수를 세지 않는다") {
                countsByIds shouldBe 0
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
