package com.petqua.application.announcement

import com.petqua.domain.announcement.Announcement
import com.petqua.domain.announcement.AnnouncementRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.mockito.Mockito.atMost
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL

@TestConstructor(autowireMode = ALL)
@SpringBootTest(webEnvironment = NONE)
class AnnouncementServiceTest(
    private var announcementService: AnnouncementService,
    @SpyBean private var announcementRepository: AnnouncementRepository,
) : BehaviorSpec({

    Given("공지사항 조회 테스트") {
        announcementRepository.saveAll(
            listOf(
                Announcement(title = "titleA", linkUrl = "linkUrlA"),
                Announcement(title = "titleB", linkUrl = "linkUrlB"),
                Announcement(title = "titleC", linkUrl = "linkUrlC"),
            )
        )

        When("공지사항을 전체 조회 하면") {
            val results = announcementService.readAll()

            Then("모든 공지사항이 조회 된다") {
                results.size shouldBe 3
            }
        }

        When("공지사항이 캐싱 되어 있으면") {
            repeat(5) { announcementService.readAll() }

            Then("퀴리가 발생 하지 않는다") {
                verify(announcementRepository, atMost(1)).findAll()
            }
        }
    }
})
