package com.petqua.presentation

import com.petqua.application.OauthService
import com.petqua.domain.oauth.OauthServerType
import org.springframework.web.bind.annotation.*

@RequestMapping("/auth")
@RestController
class OauthController(
    private val oauthService: OauthService
) {

    @GetMapping("/login/{oauthServerType}")
    fun login(
        @PathVariable oauthServerType: OauthServerType,
        @RequestParam("code") code: String,
    ): OauthResponse {
        return oauthService.login(oauthServerType, code)
    }
}
