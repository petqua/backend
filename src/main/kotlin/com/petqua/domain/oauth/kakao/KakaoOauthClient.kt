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
    private val kakaoOauthProperties: KakaoOauthProperties,
    private val kakaoOauthApiClient: KakaoOauthApiClient,
) : OauthClient {

    override fun oauthServerType(): OauthServerType {
        return KAKAO
    }

    override fun requestOauthUserInfo(oauthTokenInfo: OauthTokenInfo): OauthUserInfo {
        val kakaoOauthUserInfo = kakaoOauthApiClient.fetchUserInfo("Bearer ${oauthTokenInfo.accessToken}")
        return kakaoOauthUserInfo.toOauthUserInfo()
    }

    override fun requestToken(code: String): OauthTokenInfo {
        val tokenRequestBody: MultiValueMap<String, String> = LinkedMultiValueMap()
        tokenRequestBody.add("grant_type", "authorization_code")
        tokenRequestBody.add("client_id", kakaoOauthProperties.clientId)
        tokenRequestBody.add("redirect_uri", kakaoOauthProperties.redirectUri)
        tokenRequestBody.add("code", code)
        tokenRequestBody.add("client_secret", kakaoOauthProperties.clientSecret)

        return kakaoOauthApiClient.fetchToken(tokenRequestBody)
    }
}
