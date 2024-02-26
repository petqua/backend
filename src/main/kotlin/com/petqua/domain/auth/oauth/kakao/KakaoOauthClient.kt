package com.petqua.domain.auth.oauth.kakao

import com.petqua.domain.auth.oauth.OauthClient
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.oauth.OauthServerType.KAKAO
import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.domain.auth.oauth.OauthUserInfo
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
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
        val tokenRequestBody = HashMap<String, String>()
        tokenRequestBody["grant_type"] = "authorization_code"
        tokenRequestBody["client_id"] = kakaoOauthProperties.clientId
        tokenRequestBody["redirect_uri"] = kakaoOauthProperties.redirectUri
        tokenRequestBody["code"] = code
        tokenRequestBody["client_secret"] = kakaoOauthProperties.clientSecret

        return kakaoOauthApiClient.fetchToken(tokenRequestBody)
    }

    override fun updateToken(refreshToken: String): OauthTokenInfo {
        val tokenRequestBody = HashMap<String, String>()
        tokenRequestBody["grant_type"] = "refresh_token"
        tokenRequestBody["client_id"] = kakaoOauthProperties.clientId
        tokenRequestBody["refresh_token"] = refreshToken
        tokenRequestBody["client_secret"] = kakaoOauthProperties.clientSecret

        return kakaoOauthApiClient.fetchToken(tokenRequestBody)
    }

    override fun disconnect(accessToken: String) {
        kakaoOauthApiClient.disconnect("Bearer $accessToken")
    }
}
