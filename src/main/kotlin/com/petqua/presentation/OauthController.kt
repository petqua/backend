package com.petqua.presentation

import com.petqua.application.oauth.OauthService
import com.petqua.domain.oauth.OauthServerType
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
    ): OauthResponse {
        return oauthService.login(oauthServerType, code)
    }
}
