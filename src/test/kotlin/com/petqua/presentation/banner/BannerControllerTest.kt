package com.petqua.presentation.banner

import com.petqua.application.banner.FindBannerResult
import com.petqua.domain.banner.Banner
import com.petqua.domain.banner.BannerRepository
import com.petqua.test.DataCleaner
import io.kotest.core.spec.style.BehaviorSpec
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BannerControllerTest(
    @LocalServerPort private val port: Int,
    private val bannerRepository: BannerRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    RestAssured.port = port

    Given("배너가 등록되어 있다.") {
        val banner = bannerRepository.saveAll(
            listOf(
                Banner(imageUrl = "imageUrlC", linkUrl = "linkUrlA"),
                Banner(imageUrl = "imageUrlB", linkUrl = "linkUrlB")
            )
        )

        When("배너 목록을 조회한다.") {
            val response = Given {
                log().all()
            } When {
                get("/banner")
            } Then {
                log().all()
            } Extract {
                response()
            }

            Then("배너 목록을 응답한다.") {
                val findBannerResponse = response.`as`(Array<FindBannerResult>::class.java)
                assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                assertThat(findBannerResponse.size).isEqualTo(2)
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
