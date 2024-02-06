package com.petqua.presentation.announcement

import com.petqua.application.announcement.AnnouncementResponse
import com.petqua.application.announcement.AnnouncementService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Announcement", description = "공지 관련 API 명세")
@RequestMapping("/announcements")
@RestController
class AnnouncementController(
    private val announcementService: AnnouncementService
) {

    @Operation(summary = "공지 조회 API", description = "공지 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "공지 목록 조회 성공")
    @GetMapping
    fun readAll(): ResponseEntity<List<AnnouncementResponse>> {
        val response = announcementService.readAll()
        return ResponseEntity.ok(response)
    }
}
