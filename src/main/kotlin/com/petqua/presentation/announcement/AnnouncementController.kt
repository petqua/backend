package com.petqua.presentation.announcement

import com.petqua.application.announcement.AnnouncementService
import com.petqua.application.announcement.FindAnnouncementResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/announcement")
@RestController
class AnnouncementController(
    private val announcementService: AnnouncementService
) {

    @GetMapping
    fun getAnnouncements(): ResponseEntity<List<FindAnnouncementResult>> {
        val announcements = announcementService.getAnnouncementsList()
        return ResponseEntity.ok(announcements)
    }
}
