package com.petqua.domain.oauth

import io.jsonwebtoken.Header.JWT_TYPE
import io.jsonwebtoken.Header.TYPE
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtProvider(
    @Value("\${token.secretKey}")
    private val secretKeyText: String
) {

    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretKeyText.toByteArray(StandardCharsets.UTF_8))

    fun createToken(subject: String, tokenLiveTime: Long): String {
        val now = Date()
        val expirationTime = Date(now.time + tokenLiveTime)
        return Jwts.builder()
            .setHeaderParam(TYPE, JWT_TYPE)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expirationTime)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }
}
