package com.petqua.application.announcement

import com.petqua.domain.announcement.AnnouncementRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AnnouncementService(
    private val announcementRepository: AnnouncementRepository,
) {

    @Cacheable("announcements")
    @Transactional(readOnly = true)
    fun getAnnouncementsList(): List<FindAnnouncementResult> {
        val announcements = announcementRepository.findAll()
        return announcements.map { FindAnnouncementResult.from(it) }
    }
}
