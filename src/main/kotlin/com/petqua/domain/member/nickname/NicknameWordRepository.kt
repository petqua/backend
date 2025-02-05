package com.petqua.domain.member.nickname

import org.springframework.data.jpa.repository.JpaRepository

interface NicknameWordRepository : JpaRepository<NicknameWord, Long> {

    //    @Cacheable("nicknameWords")
    override fun findAll(): List<NicknameWord>
}
