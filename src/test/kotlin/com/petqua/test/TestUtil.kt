package com.petqua.test

import io.restassured.specification.RequestSpecification

fun RequestSpecification.authorize(accessToken: String?): RequestSpecification? {
    return accessToken?.let { auth().preemptive().oauth2(it) }
}
