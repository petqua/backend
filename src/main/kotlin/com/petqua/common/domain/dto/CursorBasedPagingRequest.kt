package com.petqua.common.domain.dto

const val DEFAULT_LAST_VIEWED_ID = -1L
const val PAGING_LIMIT_CEILING = 20
const val PADDING_FOR_HAS_NEXT_PAGE = 1

data class CursorBasedPagingRequest internal constructor(
    val lastViewedId: Long? = null,
    val limit: Int = PAGING_LIMIT_CEILING,
) {

    companion object {
        fun of(lastViewedId: Long, limit: Int): CursorBasedPagingRequest {
            val adjustedLastViewedId = if (lastViewedId == DEFAULT_LAST_VIEWED_ID) null else lastViewedId
            val adjustedLimit = if (limit > PAGING_LIMIT_CEILING) PAGING_LIMIT_CEILING else limit
            return CursorBasedPagingRequest(adjustedLastViewedId, adjustedLimit + PADDING_FOR_HAS_NEXT_PAGE)
        }
    }
}
