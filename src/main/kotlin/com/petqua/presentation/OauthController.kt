package com.petqua.presentation

import com.petqua.application.auth.OauthService
import com.petqua.domain.auth.OauthServerType
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/oauth")
@RestController
class OauthController(
    private val oauthService: OauthService
) {

    @GetMapping("/{oauthServerType}")
    fun redirectToAuthCodeRequestUrl(
        @PathVariable oauthServerType: OauthServerType,
    ): ResponseEntity<Void> {
        val redirectUri = oauthService.getAuthCodeRequestUrl(oauthServerType)
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(redirectUri)
            .build()
    }

    @GetMapping("/login/{oauthServerType}")
    fun login(
        @PathVariable oauthServerType: OauthServerType,
        @RequestParam("code") code: String,
    ): ResponseEntity<OauthResponse> {
        val oauthResponse = oauthService.login(oauthServerType, code)
        return ResponseEntity
            .ok()
            .header(SET_COOKIE, oauthResponse.refreshToken)
            .body(oauthResponse)
    }
}
