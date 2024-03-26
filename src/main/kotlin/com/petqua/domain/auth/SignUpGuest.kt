package com.petqua.domain.auth

import io.swagger.v3.oas.annotations.Hidden

@Hidden
data class SignUpGuest(
    val authMemberId: Long,
)
