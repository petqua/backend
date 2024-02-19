package com.petqua.application.banner

import com.petqua.domain.banner.Banner
import com.petqua.domain.banner.BannerRepository
import com.petqua.test.DataCleaner
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.atMost
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.boot.test.mock.mockito.SpyBean

@SpringBootTest(webEnvironment = NONE)
class BannerServiceTest(
    private val bannerService: BannerService,
    @SpyBean private val bannerRepository: BannerRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("Banner 조회 테스트") {
        bannerRepository.saveAll(
            listOf(
                Banner(imageUrl = "imageUrlA", linkUrl = "linkUrlA"),
                Banner(imageUrl = "imageUrlB", linkUrl = "linkUrlB"),
                Banner(imageUrl = "imageUrlC", linkUrl = "linkUrlC"),
            )
        )

        When("Banner를 전체 조회 하면") {
            val results = bannerService.readAll()

            Then("모든 Banner가 조회 된다") {
                results.size shouldBe 3
            }
        }

        When("Banner가 캐싱 되어 있으면") {
            repeat(5) { bannerService.readAll() }

            Then("퀴리가 발생 하지 않는다") {
                verify(bannerRepository, atMost(1)).findAll()
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
