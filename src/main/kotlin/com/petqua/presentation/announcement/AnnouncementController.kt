package com.petqua.presentation.announcement

import com.petqua.application.announcement.AnnouncementResponse
import com.petqua.application.announcement.AnnouncementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/announcements")
@RestController
class AnnouncementController(
    private val announcementService: AnnouncementService
) {

    @GetMapping
    fun readAll(): ResponseEntity<List<AnnouncementResponse>> {
        val response = announcementService.readAll()
        return ResponseEntity.ok(response)
    }
}
