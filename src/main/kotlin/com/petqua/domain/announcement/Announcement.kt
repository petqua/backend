package com.petqua.domain.announcement

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
class Announcement(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val linkUrl: String,

    @CreatedDate
    val createAt: LocalDateTime = LocalDateTime.now(), //  TODO: BaseEntity 상속

    @LastModifiedDate
    val updateAt: LocalDateTime = LocalDateTime.now(), //  TODO: BaseEntity 상속
)
