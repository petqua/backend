package com.petqua.domain.auth.kakao

import com.petqua.domain.auth.OauthClient
import com.petqua.domain.auth.OauthServerType
import com.petqua.domain.auth.OauthServerType.KAKAO
import com.petqua.domain.auth.OauthTokenInfo
import com.petqua.domain.auth.OauthUserInfo
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@ConfigurationProperties(prefix = "oauth.client.kakao")
data class KakaoOauthProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)

@EnableConfigurationProperties(KakaoOauthProperties::class)
@Component
class KakaoOauthClient(
    private val kakaoOauthProperties: KakaoOauthProperties,
    private val kakaoOauthApiClient: KakaoOauthApiClient,
) : OauthClient {
    override fun getAuthCodeRequestUrl(): URI {
        return UriComponentsBuilder
            .fromUriString("https://kauth.kakao.com/oauth/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", kakaoOauthProperties.clientId)
            .queryParam("redirect_uri", kakaoOauthProperties.redirectUri)
            .build()
            .toUri()
    }

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
