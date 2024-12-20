package com.petqua.application.product.dto

import com.petqua.common.domain.Money
import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PADDING_FOR_HAS_NEXT_PAGE
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.keyword.ProductKeyword
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.product.dto.ProductSearchCondition
import com.petqua.domain.product.dto.ProductWithInfoResponse
import com.petqua.domain.product.review.ProductReviewRecommendation
import io.swagger.v3.oas.annotations.media.Schema

data class ProductDetailResponse(
    @Schema(
        description = "상품 id",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "상품 이름",
        example = "알비노 풀레드 아시안 고정구피"
    )
    val name: String,

    @Schema(
        description = "상품 카테고리 어과",
        example = "난태생, 송사리과"
    )
    val family: String,

    @Schema(
        description = "상품 카테고리 어종",
        example = "고정구피"
    )
    val species: String,

    @Schema(
        description = "상품 가격",
        example = "30000"
    )
    val price: Money,

    @Schema(
        description = "상품 판매점 id",
        example = "1"
    )
    val storeId: Long,

    @Schema(
        description = "상품 판매점",
        example = "S아쿠아"
    )
    val storeName: String,

    @Schema(
        description = "가격 할인율",
        example = "30"
    )
    val discountRate: Int,

    @Schema(
        description = "할인 가격(판매 가격)",
        example = "21000"
    )
    val discountPrice: Money,

    @Schema(
        description = "찜 개수",
        example = "23"
    )
    val wishCount: Int,

    @Schema(
        description = "리뷰 개수",
        example = "50"
    )
    val reviewCount: Int,

    @Schema(
        description = "리뷰 평균 점수",
        example = "5"
    )
    val reviewAverageScore: Double,

    @Schema(
        description = "상품 이미지 목록",
        example = "[image1.jpeg, image2.jpeg]"
    )
    val imageUrls: List<String>,

    @Schema(
        description = "상품 상세 설명 제목",
        example = "물생활 핵 인싸어, 레드 브론즈 구피"
    )
    val descriptionTitle: String,

    @Schema(
        description = "상품 상세 설명 내용",
        example = "레드 턱시도라고도 불리며 지느러미가 아름다운 구피입니다"
    )
    val descriptionContent: String,

    @Schema(
        description = "상품 상세 이미지",
        example = "[image1.jpeg, image2.jpeg]"
    )
    val descriptionImageUrls: List<String>,

    @Schema(
        description = "안전 배송 가격",
        example = "5000"
    )
    val safeDeliveryFee: Money?,

    @Schema(
        description = "일반 배송 가격",
        example = "3000"
    )
    val commonDeliveryFee: Money?,

    @Schema(
        description = "픽업 배송 가격",
        example = "0"
    )
    val pickUpDeliveryFee: Money?,

    @Schema(
        description = "수컷 추가 가격 (null인 경우 지원 X)",
        example = "0"
    )
    val maleAdditionalPrice: Money?,

    @Schema(
        description = "암컷 추가 가격 (null인 경우 지원 X)",
        example = "2000"
    )
    val femaleAdditionalPrice: Money?,

    @Schema(
        description = "사육 온도 최소",
        example = "10"
    )
    val optimalTemperatureMin: Int,

    @Schema(
        description = "사육 온도 최대",
        example = "20"
    )
    val optimalTemperatureMax: Int,

    @Schema(
        description = "사육난이도",
        example = "하"
    )
    val difficultyLevel: String,

    @Schema(
        description = "적정 수조 크기",
        example = "1자어항"
    )
    val optimalTankSize: String,

    @Schema(
        description = "성격",
        example = "사나움"
    )
    val temperament: String,

    @Schema(
        description = "찜 여부",
        example = "true"
    )
    val isWished: Boolean,
) {
    constructor(
        productWithInfoResponse: ProductWithInfoResponse,
        imageUrls: List<String>,
        descriptionImageUrls: List<String>,
        isWished: Boolean,
        maleAdditionalPrice: Money?,
        femaleAdditionalPrice: Money?,
    ) : this(
        id = productWithInfoResponse.id,
        name = productWithInfoResponse.name,
        family = productWithInfoResponse.family,
        species = productWithInfoResponse.species,
        price = productWithInfoResponse.price,
        storeId = productWithInfoResponse.storeId,
        storeName = productWithInfoResponse.storeName,
        discountRate = productWithInfoResponse.discountRate,
        discountPrice = productWithInfoResponse.discountPrice,
        wishCount = productWithInfoResponse.wishCount,
        reviewCount = productWithInfoResponse.reviewCount,
        reviewAverageScore = productWithInfoResponse.reviewAverageScore,
        imageUrls = imageUrls,
        descriptionTitle = productWithInfoResponse.descriptionTitle,
        descriptionContent = productWithInfoResponse.descriptionContent,
        descriptionImageUrls = descriptionImageUrls,
        safeDeliveryFee = productWithInfoResponse.safeDeliveryFee,
        commonDeliveryFee = productWithInfoResponse.commonDeliveryFee,
        pickUpDeliveryFee = productWithInfoResponse.pickUpDeliveryFee,
        maleAdditionalPrice = maleAdditionalPrice,
        femaleAdditionalPrice = femaleAdditionalPrice,
        optimalTemperatureMin = productWithInfoResponse.optimalTemperatureMin,
        optimalTemperatureMax = productWithInfoResponse.optimalTemperatureMax,
        difficultyLevel = productWithInfoResponse.difficultyLevel,
        optimalTankSize = productWithInfoResponse.optimalTankSize,
        temperament = productWithInfoResponse.temperament,
        isWished = isWished,
    )
}

data class ProductReadQuery(
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = Sorter.NONE,
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    val limit: Int = PAGING_LIMIT_CEILING,
    val loginMemberOrGuest: LoginMemberOrGuest,
) {
    fun toReadConditions(): ProductReadCondition {
        return ProductReadCondition.toCondition(sourceType, sorter)
    }

    fun toPaging(): CursorBasedPaging {
        return CursorBasedPaging.of(lastViewedId, limit)
    }
}

data class ProductsResponse(
    val products: List<ProductResponse>,

    @Schema(
        description = "다음 페이지 존재 여부",
        example = "true"
    )
    val hasNextPage: Boolean,

    @Schema(
        description = "조회 조건에 해당하는 전체 상품 개수",
        example = "50"
    )
    val totalProductsCount: Int,
) {
    companion object {
        fun of(products: List<ProductResponse>, limit: Int, totalProductsCount: Int): ProductsResponse {
            return if (products.size > limit) {
                ProductsResponse(products.dropLast(PADDING_FOR_HAS_NEXT_PAGE), hasNextPage = true, totalProductsCount)
            } else {
                ProductsResponse(products, hasNextPage = false, totalProductsCount)
            }
        }
    }
}

data class ProductSearchQuery(
    val word: String,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.NONE,
    val sorter: Sorter = Sorter.NONE,
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    val limit: Int = PAGING_LIMIT_CEILING,
    val loginMemberOrGuest: LoginMemberOrGuest,
) {

    fun toCondition(): ProductSearchCondition {
        return ProductSearchCondition.toCondition(
            word = word,
            deliveryMethod = deliveryMethod,
            sorter = sorter,
        )
    }

    fun toPaging(): CursorBasedPaging {
        return CursorBasedPaging.of(lastViewedId, limit)
    }
}

data class ProductKeywordQuery(
    val word: String = "",
    val limit: Int = PAGING_LIMIT_CEILING,
) {

    fun toProductKeyword(): ProductKeyword {
        return ProductKeyword(word = word)
    }
}

data class ProductKeywordResponse(
    @Schema(
        description = "추천 검색어",
        example = "구피"
    )
    val keyword: String,
)


data class UpdateReviewRecommendationCommand(
    val memberId: Long,
    val productReviewId: Long,
) {

    fun toReviewRecommendation(): ProductReviewRecommendation {
        return ProductReviewRecommendation(
            memberId = memberId,
            productReviewId = productReviewId
        )
    }
}
