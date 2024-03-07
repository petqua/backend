package com.petqua.presentation.fish

import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.fish.FishRepository
import com.petqua.domain.fish.dto.SpeciesSearchResponse
import com.petqua.exception.fish.FishExceptionType.INVALID_SPECIES_NAME
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.fish
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.restassured.common.mapper.TypeRef
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK

class FishControllerTest(
    private val fishRepository: FishRepository,
) : ApiTestConfig() {

    init {

        Given("검색어 자동완성을 통해 어종 목록을 조회할 때") {
            val fishA = fishRepository.save(fish(species = "베타"))
            val fishB = fishRepository.save(fish(species = "베일 테일"))
            val fishC = fishRepository.save(fish(species = "베일 임벨리스"))
            val fishD = fishRepository.save(fish(species = "임베리스"))

            When("어종 검색어를 입력하면") {
                val response = requestReadAutoCompleteSpecies(word = "베")

                Then("관련된 어종 목록을 조회할 수 있다") {
                    val speciesSearchResponses = response.`as`(object : TypeRef<List<SpeciesSearchResponse>>() {})

                    assertSoftly {
                        response.statusCode shouldBe OK.value()
                        speciesSearchResponses shouldBe listOf(
                            SpeciesSearchResponse(fishA.species.name),
                            SpeciesSearchResponse(fishD.species.name),
                            SpeciesSearchResponse(fishB.species.name),
                            SpeciesSearchResponse(fishC.species.name),
                        )
                    }
                }
            }

            When("어종 검색어와 개수를 입력하면") {
                val response = requestReadAutoCompleteSpecies(word = "베", limit = 2)

                Then("관련된 어종 목록을 조회할 수 있다") {
                    val speciesSearchResponses = response.`as`(object : TypeRef<List<SpeciesSearchResponse>>() {})

                    assertSoftly {
                        response.statusCode shouldBe OK.value()
                        speciesSearchResponses shouldBe listOf(
                            SpeciesSearchResponse(fishA.species.name),
                            SpeciesSearchResponse(fishD.species.name),
                        )
                    }
                }
            }

            When("어종 검색어를 공백으로 입력하면") {
                val response = requestReadAutoCompleteSpecies(word = " ")

                Then("예외를 던진다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly {
                        response.statusCode shouldBe BAD_REQUEST.value()
                        exceptionResponse.message shouldBe INVALID_SPECIES_NAME.errorMessage()
                    }
                }
            }

            When("어종 검색어를 입력하지 않으면") {
                val response = requestReadAutoCompleteSpecies(word = null)

                Then("예외를 던진다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly {
                        response.statusCode shouldBe BAD_REQUEST.value()
                        exceptionResponse.message shouldContain "Parameter specified as non-null is null"
                    }
                }
            }
        }
    }
}
