package com.petqua.domain.banner

import org.springframework.data.jpa.repository.JpaRepository

interface BannerRepository: JpaRepository<Banner, Long> {
}
