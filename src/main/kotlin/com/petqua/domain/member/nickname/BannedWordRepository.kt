package com.petqua.domain.member.nickname

import org.springframework.data.jpa.repository.JpaRepository

interface BannedWordRepository : JpaRepository<BannedWord, Long> {
}
