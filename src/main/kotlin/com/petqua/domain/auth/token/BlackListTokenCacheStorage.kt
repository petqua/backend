package com.petqua.domain.auth.token

import java.util.concurrent.TimeUnit
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository


private fun blackListKeyByMemberId(memberId: Long) = "member:$memberId:accessToken"

@Repository
class BlackListTokenCacheStorage(
    private val redisTemplate: RedisTemplate<String, String>,
    private val authTokenProperties: AuthTokenProperties,
) {

    fun save(memberId: Long, accessToken: String) {
        redisTemplate.opsForValue().set(
            blackListKeyByMemberId(memberId),
            accessToken,
            authTokenProperties.accessTokenLiveTime,
            TimeUnit.MILLISECONDS,
        )
    }

    fun isBlackListed(memberId: Long, accessToken: String): Boolean {
        val blackListToken = redisTemplate.opsForValue().get(blackListKeyByMemberId(memberId))
        return blackListToken == accessToken
    }
}
