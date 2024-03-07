package com.petqua.presentation.notification

import com.petqua.application.notification.dto.ReadAllNotificationResponse
import com.petqua.domain.notification.NotificationRepository
import com.petqua.presentation.notification.dto.ReadAllNotificationRequest
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.notification
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN

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

        Given("회원에게 등록 된 읽지 않은 알림의 개수를") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)

            notificationRepository.saveAll(
                listOf(
                    notification(memberId = memberId, title = "알림1", isRead = false),
                    notification(memberId = memberId, title = "알림2", isRead = false),
                    notification(memberId = memberId, title = "알림3", isRead = false),
                    notification(memberId = memberId, title = "알림4", isRead = true),
                    notification(memberId = memberId, title = "알림5", isRead = true),
                )
            )

            When("조회 할 수 있다") {
                val response = requestCountUnreadNotification(
                    accessToken = accessToken,
                )

                Then("읽지 않은 알림의 개수가 조회 된다") {
                    response.`as`(Int::class.java) shouldBe 3
                }
            }
        }

        Given("알림을 읽음 처리 할 수 있다") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)

            val notification = notificationRepository.save(
                notification(memberId = memberId, title = "알림1", isRead = false)
            )

            When("알림을 읽음 처리 하면") {
                requestCheckNotification(
                    accessToken = accessToken,
                    notificationId = notification.id,
                )

                Then("알림이 읽음 처리 된다") {
                    val findNotification = notificationRepository.findById(notification.id).get()
                    findNotification.isRead shouldBe true
                }
            }

            When("읽지 않은 알림의 개수가 갱신 된다") {
                val previousUnreadNotificationCount = requestCountUnreadNotification(accessToken).`as`(Int::class.java)
                requestCheckNotification(
                    accessToken = accessToken,
                    notificationId = notification.id,
                )

                Then("읽지 않은 알림의 개수가 갱신 된다") {
                    val count = requestCountUnreadNotification(accessToken).`as`(Int::class.java)
                    count shouldBe previousUnreadNotificationCount - 1
                }
            }

            When("존재 하지 않는 알림을 확인 하면") {
                Then("알림을 확인 할 수 없다") {
                    val response = requestCheckNotification(
                        accessToken = accessToken,
                        notificationId = Long.MIN_VALUE,
                    )
                    response.statusCode shouldBe BAD_REQUEST.value()
                }
            }

            When("다른 회원의 알림을 확인 하면") {
                Then("알림을 확인 할 수 없다") {
                    val otherMemberAccessToken = signInAsMember().accessToken
                    val response = requestCheckNotification(
                        accessToken = otherMemberAccessToken,
                        notificationId = notification.id,
                    )
                    response.statusCode shouldBe FORBIDDEN.value()
                }
            }
        }


    }
}
