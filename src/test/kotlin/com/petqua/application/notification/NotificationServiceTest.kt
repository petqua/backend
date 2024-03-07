package com.petqua.application.notification

import com.petqua.application.notification.dto.ReadAllNotificationQuery
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.notification.NotificationRepository
import com.petqua.exception.notification.NotificationException
import com.petqua.exception.notification.NotificationExceptionType.FORBIDDEN_NOTIFICATION
import com.petqua.exception.notification.NotificationExceptionType.NOTIFICATION_NOT_FOUND
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.notification
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class NotificationServiceTest(
    private val notificationService: NotificationService,
    private val notificationRepository: NotificationRepository,
    private val memberRepository: MemberRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("회원을 기준으로 알림을 조회 할 수 있다.") {
        val memberId = memberRepository.save(member()).id

        notificationRepository.saveAll(
            listOf(
                notification(memberId = memberId, title = "알림1"),
                notification(memberId = memberId, title = "알림2"),
                notification(memberId = memberId, title = "알림3"),
                notification(memberId = memberId, title = "알림4"),
                notification(memberId = memberId, title = "알림5"),
            )
        )

        When("알림을 조회 하면") {
            val query = ReadAllNotificationQuery(memberId = memberId)
            val response = notificationService.readAllNotification(query)

            Then("최신순으로 조회 된다.") {
                assertSoftly(response) {
                    notifications.sortedByDescending { it.createdAt }
                }
            }
        }

        When("조회 조건을 포함 하면") {
            val query = ReadAllNotificationQuery(memberId = memberId, lastViewedId = 4, limit = 2)
            val response = notificationService.readAllNotification(query)

            Then("마지막으로 조회한 알림 이후의 알림만 조회 된다.") {
                assertSoftly(response) {
                    notifications.sortedByDescending { it.createdAt }
                    notifications.first().id shouldBe 3
                    hasNextPage shouldBe true
                }
            }
        }
    }

    Given("회원을 기준으로 읽지 않은 알림의 개수를 조회 할 수 있다.") {
        val memberId = memberRepository.save(member()).id

        notificationRepository.saveAll(
            listOf(
                notification(memberId = memberId, title = "알림1", isRead = false),
                notification(memberId = memberId, title = "알림2", isRead = false),
                notification(memberId = memberId, title = "알림3", isRead = false),
                notification(memberId = memberId, title = "알림4", isRead = true),
                notification(memberId = memberId, title = "알림5", isRead = true),
            )
        )

        When("읽지 않은 알림의 개수를 조회 하면") {
            val count = notificationService.countUnreadNotifications(memberId)

            Then("읽지 않은 알림의 개수가 조회 된다.") {
                count shouldBe 3
            }
        }
    }

    Given("회원을 기준으로 알림을 확인 할 수 있다.") {
        val memberId = memberRepository.save(member()).id

        When("알림을 확인 하면") {
            val notificationId = notificationRepository.save(notification(memberId = memberId)).id
            notificationService.checkNotification(memberId, notificationId)

            Then("알림이 읽음 처리 된다.") {
                val notification = notificationRepository.findById(notificationId).get()
                notification.isRead shouldBe true
            }
        }

        When("알림이 읽음 처리 되면") {
            val notificationId = notificationRepository.save(notification(memberId = memberId)).id
            val previousUnreadNotificationCount = notificationService.countUnreadNotifications(memberId)
            notificationService.checkNotification(memberId, notificationId)

            Then("읽지 않은 알림의 개수가 갱신 된다.") {
                val count = notificationService.countUnreadNotifications(memberId)
                count shouldBe 0
            }
        }

        When("존재 하지 않는 알림을 확인 하면") {
            Then("알림을 확인 할 수 없다.") {
                shouldThrow<NotificationException> {
                    notificationService.checkNotification(memberId, Long.MIN_VALUE)
                }.exceptionType() shouldBe NOTIFICATION_NOT_FOUND
            }
        }

        When("다른 사람의 알림을 확인 하면") {
            val notificationId = notificationRepository.save(notification(memberId = memberId)).id
            val otherMemberId = memberRepository.save(member()).id

            Then("알림을 확인 할 수 없다.") {
                shouldThrow<NotificationException> {
                    notificationService.checkNotification(otherMemberId, notificationId)
                }.exceptionType() shouldBe FORBIDDEN_NOTIFICATION
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
