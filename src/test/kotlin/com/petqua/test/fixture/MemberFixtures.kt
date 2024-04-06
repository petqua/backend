package com.petqua.test.fixture

import com.petqua.domain.auth.Authority
import com.petqua.domain.member.FishLifeYear
import com.petqua.domain.member.Member

fun member(
    id: Long = 0L,
    authMemberId: Long = 0L,
    authority: Authority = Authority.MEMBER,
    nickname: String = "nickname",
    profileImageUrl: String = "imageUrl",
    fishTankCount: Int = 1,
    fishLifeYear: Int = 1,
    isAnonymous: Boolean = false,
    hasAgreedToMarketingNotification: Boolean = false,
    isDeleted: Boolean = false,
): Member {
    return Member(
        id = id,
        authMemberId = authMemberId,
        authority = authority,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        fishTankCount = fishTankCount,
        fishLifeYear = FishLifeYear.from(fishLifeYear),
        isAnonymous = isAnonymous,
        hasAgreedToMarketingNotification = hasAgreedToMarketingNotification,
        isDeleted = isDeleted,
    )
}
