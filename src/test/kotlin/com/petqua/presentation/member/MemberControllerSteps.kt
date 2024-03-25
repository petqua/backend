package com.petqua.presentation.member

import com.petqua.presentation.member.dto.MemberAddProfileRequest
import com.petqua.presentation.member.dto.MemberSignUpRequest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

fun requestSignUp(
    signUpToken: String,
    memberSignUpRequest: MemberSignUpRequest,
): Response {
    return Given {
        log().all()
        header("Sign-Up-Authorization", signUpToken)
        contentType(APPLICATION_JSON_VALUE)
        body(memberSignUpRequest)
    } When {
        post("/members/sign-up")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestAddProfile(
    request: MemberAddProfileRequest,
    accessToken: String,
): Response {
    return Given {
        log().all()
        contentType(APPLICATION_JSON_VALUE)
        body(request)
        auth().preemptive().oauth2(accessToken)
    } When {
        post("/members/profiles")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
