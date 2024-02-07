package com.petqua.test.fixture

import com.petqua.domain.auth.Authority
import com.petqua.domain.member.Member

fun member(
    id: Long = 0L,
    oauthId: String = "oauthId",
    oauthServerNumber: Int = 1,
    authority: Authority = Authority.MEMBER,
    nickname: String = "nickname",
    profileImageUrl: String = "profile.jpg",
    fishBowlCount: Int = 0,
    years: Int = 1,
): Member {
    return Member(
        id,
        oauthId,
        oauthServerNumber,
        authority,
        nickname,
        profileImageUrl,
        fishBowlCount,
        years,
    )
}
