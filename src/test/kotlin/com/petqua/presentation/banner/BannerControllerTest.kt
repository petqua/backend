package com.petqua.presentation.banner

import com.petqua.application.banner.dto.BannerResponse
import com.petqua.domain.banner.Banner
import com.petqua.domain.banner.BannerRepository
import com.petqua.test.ApiTestConfig
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.springframework.http.HttpStatus

class BannerControllerTest(
    private val bannerRepository: BannerRepository
) : ApiTestConfig() {
    init {
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
                    get("/banners")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("배너 목록을 응답한다.") {
                    val findBannerResponse = response.`as`(Array<BannerResponse>::class.java)

                    assertSoftly {
                        it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                        it.assertThat(findBannerResponse.size).isEqualTo(2)
                    }
                }
            }
        }
    }
}
