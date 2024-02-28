package com.petqua.test.fixture

import com.petqua.domain.auth.Authority
import com.petqua.domain.member.Member
import java.time.LocalDateTime

fun member(
    id: Long = 0L,
    oauthId: Long = 1L,
    oauthServerNumber: Int = 1,
    authority: Authority = Authority.MEMBER,
    nickname: String = "nickname",
    profileImageUrl: String = "imageUrl",
    fishBowlCount: Int = 0,
    years: Int = 1,
    isDeleted: Boolean = false,
    oauthAccessToken: String = "oauthAccessToken",
    oauthAccessTokenExpiresAt: LocalDateTime = LocalDateTime.now().plusSeconds(21599),
    oauthRefreshToken: String = "oauthRefreshToken",
): Member {
    return Member(
        id = id,
        oauthId = oauthId,
        oauthServerNumber = oauthServerNumber,
        authority = authority,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        fishBowlCount = fishBowlCount,
        years = years,
        isDeleted = isDeleted,
        oauthAccessToken = oauthAccessToken,
        oauthAccessTokenExpiresAt = oauthAccessTokenExpiresAt,
        oauthRefreshToken = oauthRefreshToken,
    )
}
