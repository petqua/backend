package com.petqua.presentation.auth

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response

fun requestLogin(
    code: String
): Response {
    return Given {
        log().all()
    } When {
        queryParam("code", code)
        get("/auth/login/kakao")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestExtendLogin(
    accessToken: String,
    refreshToken: String,
): Response {
    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
        cookie("refresh-token", refreshToken)
    } When {
        get("/auth/token")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestDeleteMember(
    accessToken: String
): Response {
    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
    } When {
        delete("/auth/members")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
