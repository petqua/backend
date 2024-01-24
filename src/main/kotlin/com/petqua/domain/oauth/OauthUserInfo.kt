package com.petqua.domain.oauth

interface OauthUserInfo {

    fun nickname() :String

    fun imageUrl() :String

    fun oauthId(): String
}
