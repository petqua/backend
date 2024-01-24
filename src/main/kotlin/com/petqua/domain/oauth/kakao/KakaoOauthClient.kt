package com.petqua.domain.oauth.kakao

import com.petqua.domain.oauth.OauthClient
import com.petqua.domain.oauth.OauthServerType
import com.petqua.domain.oauth.OauthServerType.KAKAO
import com.petqua.domain.oauth.OauthTokenInfo
import com.petqua.domain.oauth.OauthUserInfo
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@Component
class KakaoOauthClient(
    private val kakaoOauthConfig: KakaoOauthConfig,
    private val kakaoOauthApiClient: KakaoOauthApiClient,
) : OauthClient {

    override fun oauthServerType(): OauthServerType {
        return KAKAO
    }

    override fun requestOauthUserInfo(code: String): OauthUserInfo {
        val oauthTokenInfo = requestToken(code)
        return kakaoOauthApiClient.fetchUserInfo("Bearer " + oauthTokenInfo.accessToken)
    }

    private fun requestToken(code: String): OauthTokenInfo {
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("grant_type", "authorization_code")
        body.add("client_id", kakaoOauthConfig.clientId)
        body.add("redirect_uri", kakaoOauthConfig.redirectUri)
        body.add("code", code)
        body.add("client_secret", kakaoOauthConfig.clientSecret)

        return kakaoOauthApiClient.fetchToken(body)
    }
}
