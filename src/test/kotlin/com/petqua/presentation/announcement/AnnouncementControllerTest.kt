package com.petqua.presentation.announcement

import com.petqua.application.announcement.AnnouncementResponse
import com.petqua.domain.announcement.Announcement
import com.petqua.domain.announcement.AnnouncementRepository
import com.petqua.test.ApiTestConfig
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.springframework.http.HttpStatus

class AnnouncementControllerTest(
    private val announcementRepository: AnnouncementRepository
) : ApiTestConfig() {

    init {
        Given("공지 사항이 존재할 때") {
            val announcements = announcementRepository.saveAll(
                listOf(
                    Announcement(title = "announcementsA", linkUrl = "linkUrlA"),
                    Announcement(title = "announcementsB", linkUrl = "linkUrlB")
                )
            )

            When("공지 사항 목록을 조회하면") {
                val response = Given {
                    log().all()
                } When {
                    get("/announcements")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("모든 공지 사항 목록이 반환된다.") {
                    val responseData = response.`as`(Array<AnnouncementResponse>::class.java)

                    assertSoftly {
                        it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                        it.assertThat(responseData.size).isEqualTo(2)
                    }
                }
            }
        }
    }
}
