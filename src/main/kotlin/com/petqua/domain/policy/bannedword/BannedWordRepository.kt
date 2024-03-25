package com.petqua.domain.policy.bannedword

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository

interface BannedWordRepository : JpaRepository<BannedWord, Long> {

    @Cacheable("bannedWords")
    override fun findAll(): List<BannedWord>
}
