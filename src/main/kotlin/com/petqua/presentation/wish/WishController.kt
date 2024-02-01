package com.petqua.presentation.wish

import com.petqua.application.wish.DeleteWishCommand
import com.petqua.application.wish.WishService
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/wish")
@RestController
class WishController(
    private val wishService: WishService
) {

    @PostMapping
    fun save(
        @Auth loginMember: LoginMember,
        @RequestBody request: SaveWishRequest
    ): ResponseEntity<Void> {
        val command = request.toCommand(loginMember.memberId)
        wishService.save(command)
        return ResponseEntity
            .noContent()
            .build()
    }

    @GetMapping
    fun readAll(@Auth loginMember: LoginMember): ResponseEntity<List<WishResponse>> {
        val responses = wishService.readAll(loginMember.memberId)
        return ResponseEntity.ok(responses)
    }

    @DeleteMapping("/{wishId}")
    fun delete(
        @Auth loginMember: LoginMember,
        @PathVariable wishId: Long,
    ): ResponseEntity<Void> {
        val command = DeleteWishCommand(
            memberId = loginMember.memberId,
            wishId = wishId
        )
        wishService.delete(command)
        return ResponseEntity
            .noContent()
            .build()
    }
}
