package com.petqua.domain.product.dto

import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.member.Member
import com.petqua.domain.order.Order
import com.petqua.domain.order.OrderPayment
import com.petqua.domain.order.OrderStatus
import com.petqua.domain.product.option.Sex
import com.petqua.domain.product.review.ProductReview
import com.petqua.domain.product.review.ProductReviewContent
import com.petqua.domain.product.review.ProductReviewScore
import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import java.time.LocalDateTime

data class ProductReviewReadCondition(
    val productId: Long,
    val sorter: ProductReviewSorter = REVIEW_DATE_DESC,
    val score: Int? = null,
    val photoOnly: Boolean,
)

data class ProductReviewWithMemberResponse(
    val id: Long,
    val productId: Long,
    val score: Int,
    val content: String,
    val createdAt: LocalDateTime,
    val hasPhotos: Boolean,
    val recommendCount: Int,
    val reviewerId: Long,
    val reviewerName: String,
    val reviewerProfileImageUrl: String?,
    val reviewerFishTankCount: Int, // FIXME: 회원 수조 개수
    val reviewerYears: Int, // FIXME: 회원 가입 연차
) {

    constructor(productReview: ProductReview, reviewer: Member) : this(
        id = productReview.id,
        productId = productReview.productId,
        score = productReview.score.value,
        content = productReview.content.value,
        createdAt = productReview.createdAt,
        hasPhotos = productReview.hasPhotos,
        recommendCount = productReview.recommendCount,
        reviewerId = reviewer.id,
        reviewerName = reviewer.nickname.value,
        reviewerProfileImageUrl = reviewer.profileImageUrl,
        reviewerFishTankCount = reviewer.fishTankCount,
        reviewerYears = reviewer.fishLifeYear.value,
    )
}

data class MemberProductReview(
    val reviewId: Long,
    val memberId: Long,
    val createdAt: LocalDateTime,
    val orderStatus: OrderStatus,
    val storeId: Long,
    val storeName: String,
    val productId: Long,
    val productName: String,
    val productThumbnailUrl: String,
    val quantity: Int,
    val sex: Sex,
    val deliveryMethod: DeliveryMethod,
    val score: ProductReviewScore,
    val content: ProductReviewContent,
    val recommendCount: Int,
) {
    constructor(
        productReview: ProductReview,
        order: Order,
        orderPayment: OrderPayment,
    ) : this(
        reviewId = productReview.id,
        memberId = productReview.memberId,
        createdAt = order.createdAt,
        orderStatus = orderPayment.status,
        storeId = order.orderProduct.storeId,
        storeName = order.orderProduct.storeName,
        productId = order.orderProduct.productId,
        productName = order.orderProduct.productName,
        productThumbnailUrl = order.orderProduct.thumbnailUrl,
        quantity = order.orderProduct.quantity,
        sex = order.orderProduct.sex,
        deliveryMethod = order.orderProduct.deliveryMethod,
        score = productReview.score,
        content = productReview.content,
        recommendCount = productReview.recommendCount,
    )
}

data class ProductReviewScoreWithCount(
    val score: Int,
    val count: Long,
)
