package com.petqua.presentation

data class OauthResponse(
    private val accessToken: String,
    val refreshToken: String,
)
