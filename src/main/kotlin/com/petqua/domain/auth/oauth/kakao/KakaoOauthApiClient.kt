package com.petqua.domain.auth.oauth.kakao

import com.petqua.domain.auth.oauth.OauthIdInfo
import com.petqua.domain.auth.oauth.OauthTokenInfo
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange

interface KakaoOauthApiClient {

    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    fun fetchToken(@RequestParam body: Map<String, String>): OauthTokenInfo

    @GetExchange(url = "https://kapi.kakao.com/v2/user/me")
    fun fetchUserInfo(@RequestHeader(name = AUTHORIZATION) bearerToken: String): KakaoUserInfo

    @PostExchange(url = "https://kapi.kakao.com/v1/user/unlink", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    fun disconnect(@RequestHeader(name = AUTHORIZATION) accessToken: String): OauthIdInfo
}
