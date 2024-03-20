package com.petqua.test.fixture

import com.petqua.domain.auth.AuthMember
import com.petqua.domain.auth.oauth.OauthServerType.KAKAO
import java.time.LocalDateTime

fun authMember(
    id: Long = 0L,
    oauthId: Long = 1L,
    oauthServerNumber: Int = KAKAO.number,
    oauthAccessToken: String = "oauthAccessToken",
    oauthAccessTokenExpiresAt: LocalDateTime = LocalDateTime.now().plusSeconds(21599),
    oauthRefreshToken: String = "oauthRefreshToken",
    isDeleted: Boolean = false,
): AuthMember {
    return AuthMember(
        id = id,
        oauthId = oauthId,
        oauthServerNumber = oauthServerNumber,
        oauthAccessToken = oauthAccessToken,
        oauthAccessTokenExpiresAt = oauthAccessTokenExpiresAt,
        oauthRefreshToken = oauthRefreshToken,
        isDeleted = isDeleted,
    )
}
