package com.petqua.domain.auth.kakao

import com.petqua.domain.auth.OauthTokenInfo
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface KakaoOauthApiClient {

    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    fun fetchToken(@RequestParam body: MultiValueMap<String, String>): OauthTokenInfo

    @GetExchange(url = "https://kapi.kakao.com/v2/user/me")
    fun fetchUserInfo(@RequestHeader(name = AUTHORIZATION) bearerToken: String): KakaoUserInfo
}
