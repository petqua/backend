package com.petqua.presentation.notification

import com.petqua.application.notification.dto.ReadAllNotificationResponse
import com.petqua.domain.notification.NotificationRepository
import com.petqua.presentation.notification.dto.ReadAllNotificationRequest
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.notification
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe

class NotificationControllerTest(
    private val notificationRepository: NotificationRepository,
) : ApiTestConfig() {

    init {
        Given("회원에게 등록 된 알림을") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)

            notificationRepository.saveAll(
                listOf(
                    notification(memberId = memberId, title = "알림1"),
                    notification(memberId = memberId, title = "알림2"),
                    notification(memberId = memberId, title = "알림3"),
                    notification(memberId = memberId, title = "알림4"),
                    notification(memberId = memberId, title = "알림5"),
                )
            )

            When("조회 할 수 있다") {
                val response = requestReadAllNotification(
                    accessToken = accessToken,
                    readAllNotificationRequest = ReadAllNotificationRequest(
                        lastViewedId = 4,
                        limit = 2,
                    )
                )

                Then("최신순으로 조회 된다") {
                    val findNotificationResponse = response.`as`(ReadAllNotificationResponse::class.java)

                    assertSoftly(findNotificationResponse) {
                        notifications.sortedByDescending { it.createdAt }
                        notifications.size shouldBe 2
                        hasNextPage shouldBe true
                    }
                }
            }

            When("조회 조건을 입력 하지 않으면") {
                val response = requestReadAllNotification(
                    accessToken = accessToken,
                    readAllNotificationRequest = ReadAllNotificationRequest(),
                )

                Then("지정된 기본값으로 조회 된다") {
                    val findNotificationResponse = response.`as`(ReadAllNotificationResponse::class.java)

                    assertSoftly(findNotificationResponse) {
                        notifications.sortedByDescending { it.createdAt }
                        notifications.size shouldBe 5
                        hasNextPage shouldBe false
                    }
                }
            }
        }
    }
}
