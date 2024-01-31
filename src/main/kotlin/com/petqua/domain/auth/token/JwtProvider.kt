package com.petqua.domain.auth.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Header.JWT_TYPE
import io.jsonwebtoken.Header.TYPE
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey


@Component
class JwtProvider(
    @Value("\${token.secret-key}")
    private val secretKeyText: String
) {

    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretKeyText.toByteArray(StandardCharsets.UTF_8))

    fun createToken(subject: String, tokenLiveTime: Long, issuedDate: Date): String {
        val expirationDate = Date(issuedDate.time + tokenLiveTime)

        return Jwts.builder()
            .setHeaderParam(TYPE, JWT_TYPE)
            .setSubject(subject)
            .setIssuedAt(issuedDate)
            .setExpiration(expirationDate)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun createToken(claims: Map<String, String>, tokenLiveTime: Long, issuedDate: Date): String {
        val expirationDate = Date(issuedDate.time + tokenLiveTime)

        return Jwts.builder()
            .setHeaderParam(TYPE, JWT_TYPE)
            .setClaims(claims)
            .setIssuedAt(issuedDate)
            .setExpiration(expirationDate)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun isValidToken(token: String): Boolean {
        try {
            parseToken(token)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun isExpiredToken(token: String): Boolean {
        try {
            parseToken(token)
        } catch (e: ExpiredJwtException) {
            return true
        }
        return false
    }

    fun getPayload(token: String): Map<String, String> {
        val tokenClaims = parseToken(token)
        return tokenClaims.body.entries.associate {(key, value) ->
            key to value.toString()
        }
    }

    fun parseToken(token: String): Jws<Claims> {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
    }
}
