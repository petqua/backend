package com.petqua.presentation.notification

import com.petqua.presentation.notification.dto.ReadAllNotificationRequest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response

fun requestReadAllNotification(
    accessToken: String,
    readAllNotificationRequest: ReadAllNotificationRequest,
): Response {
    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
        params(
            "lastViewedId", readAllNotificationRequest.lastViewedId,
            "limit", readAllNotificationRequest.limit,
        )
    } When {
        get("/notifications")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestCountUnreadNotification(
    accessToken: String,
): Response {
    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
    } When {
        get("/notifications/unread/count")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
