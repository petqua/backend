package com.petqua.application.notification

import com.petqua.application.notification.dto.ReadAllNotificationQuery
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.notification.NotificationRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.notification
import io.kotest.assertions.assertSoftly
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

    afterContainer {
        dataCleaner.clean()
    }
})
