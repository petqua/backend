package com.petqua.presentation

import com.petqua.application.auth.OauthService
import com.petqua.domain.auth.OauthServerType
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/oauth")
@RestController
class OauthController(
    private val oauthService: OauthService
) {

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
