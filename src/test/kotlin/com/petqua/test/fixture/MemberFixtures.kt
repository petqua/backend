package com.petqua.test.fixture

import com.petqua.domain.auth.Authority
import com.petqua.domain.member.Member

fun member(
    id: Long = 0L,
    oauthId: String = "oauthId",
    oauthServerNumber: Int = 1,
    authority: Authority = Authority.MEMBER
): Member {
    return Member(
        id,
        oauthId,
        oauthServerNumber,
        authority
    )
}
