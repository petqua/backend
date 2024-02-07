package com.petqua.common.config

import com.petqua.domain.announcement.Announcement
import com.petqua.domain.announcement.AnnouncementRepository
import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.banner.Banner
import com.petqua.domain.banner.BannerRepository
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.WishCount
import com.petqua.domain.product.review.ProductReview
import com.petqua.domain.product.review.ProductReviewImage
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.recommendation.ProductRecommendation
import com.petqua.domain.recommendation.ProductRecommendationRepository
import com.petqua.domain.store.Store
import com.petqua.domain.store.StoreRepository
import java.math.BigDecimal
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Profile("local", "prod")
class DataInitializer(
    private val announcementRepository: AnnouncementRepository,
    private val bannerRepository: BannerRepository,
    private val productRepository: ProductRepository,
    private val recommendationRepository: ProductRecommendationRepository,
    private val storeRepository: StoreRepository,
    private val memberRepository: MemberRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
) {

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun setUpData() {
        // announcement
        val announcement1 = Announcement(
            title = "[공지] 펫쿠아 프론트엔드 개발자 구인 중!",
            linkUrl = "https://team.petqua.co.kr/"
        )
        val announcement2 = Announcement(
            title = "[공지] 펫쿠아 복지 추가 GPT4 지원 예정",
            linkUrl = "https://team.petqua.co.kr/"
        )
        val announcement3 = Announcement(
            title = "[공지] 펫쿠아 개발팀 copilot 적극 활용",
            linkUrl = "https://team.petqua.co.kr/"
        )
        announcementRepository.saveAll(listOf(announcement1, announcement2, announcement3))

        // banner
        val banner1 = Banner(
            imageUrl = "https://docs.petqua.co.kr/banners/b08f14d5ac00721b.jpg",
            linkUrl = "https://team.petqua.co.kr/"
        )
        val banner2 = Banner(
            imageUrl = "https://docs.petqua.co.kr/banners/announcement1.jpg",
            linkUrl = "https://team.petqua.co.kr/"
        )
        val banner3 = Banner(
            imageUrl = "https://docs.petqua.co.kr/banners/announcement2.jpg",
            linkUrl = "https://team.petqua.co.kr/"
        )
        val banner4 = Banner(
            imageUrl = "https://docs.petqua.co.kr/banners/announcement3.jpg",
            linkUrl = "https://team.petqua.co.kr/"
        )
        bannerRepository.saveAll(listOf(banner1, banner2, banner3, banner4))

        // store
        val store1 = Store(name = "니모를 찾아서")
        val store2 = Store(name = "제주 미영이네 식당")
        storeRepository.saveAll(listOf(store1, store2))

        // product
        val product1 = Product(
            name = "니모",
            category = "기타과",
            price = BigDecimal.valueOf(30000L).setScale(2),
            storeId = store1.id,
            discountRate = 10,
            discountPrice = BigDecimal(27000L).setScale(2),
            wishCount = WishCount(3),
            reviewCount = 10,
            reviewTotalScore = 50,
            thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg",
            description = "니모를 찾아서 주연",
            canDeliverSafely = true,
            canDeliverCommonly = true,
            canPickUp = true,
        )
        val product2 = Product(
            name = "참고등어",
            category = "대형어",
            price = BigDecimal.valueOf(20000L).setScale(2),
            storeId = store1.id,
            discountRate = 10,
            discountPrice = BigDecimal(18000L).setScale(2),
            wishCount = WishCount(5),
            reviewCount = 3,
            reviewTotalScore = 15,
            thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail2.jpeg",
            description = "제주산",
            canDeliverSafely = true,
            canDeliverCommonly = true,
            canPickUp = true,
        )
        val product3 = Product(
            name = "니모를 찾아서 세트",
            category = "기타과",
            price = BigDecimal.valueOf(80000L).setScale(2),
            storeId = store1.id,
            discountRate = 50,
            discountPrice = BigDecimal(40000L).setScale(2),
            wishCount = WishCount(100),
            reviewCount = 50,
            reviewTotalScore = 250,
            thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg",
            description = "니모를 찾아서 주연 조연",
            canDeliverSafely = true,
            canDeliverCommonly = true,
            canPickUp = true,
        )
        productRepository.saveAll(listOf(product1, product2, product3))
        saveProducts(store1.id)

        // productRecommendation
        val productRecommendation1 = ProductRecommendation(productId = product3.id)
        recommendationRepository.saveAll(listOf(productRecommendation1))

        // member
        val member = memberRepository.save(
            Member(
                oauthId = "oauthId",
                oauthServerNumber = 1,
                authority = MEMBER,
            )
        )

// productReview
        val reviews = productReviewRepository.saveAll(
            listOf(
                ProductReview(
                    productId = product1.id,
                    memberId = member.id,
                    content = "좋아요",
                    score = 5,
                    hasPhotos = true
                ),
                ProductReview(
                    productId = product1.id,
                    memberId = member.id,
                    content = "조금 좋아요",
                    score = 4,
                    hasPhotos = false
                ),
                ProductReview(
                    productId = product1.id,
                    memberId = member.id,
                    content = "약간 좋아요",
                    score = 3,
                    hasPhotos = false
                ),
            )
        )
        reviews.find { it.hasPhotos }?.let {
            productReviewImageRepository.saveAll(
                listOf(
                    ProductReviewImage(imageUrl = "https://docs.petqua.co.kr/reviews/1.jpeg", productReviewId = it.id),
                    ProductReviewImage(imageUrl = "https://docs.petqua.co.kr/reviews/2.jpeg", productReviewId = it.id),
                    ProductReviewImage(imageUrl = "https://docs.petqua.co.kr/reviews/3.jpeg", productReviewId = it.id),
                )
            )
        }
    }

    private fun saveProducts(storeId: Long) {
        val products = (1..100).map {
            Product(
                name = "니모를 찾아서 세트$it",
                category = "기타과",
                price = BigDecimal.valueOf(80000L).setScale(2),
                storeId = storeId,
                discountRate = 50,
                discountPrice = BigDecimal(40000L).setScale(2),
                wishCount = WishCount(100),
                reviewCount = 50,
                reviewTotalScore = 250,
                thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg",
                description = "니모를 찾아서 주연 조연",
                canDeliverSafely = true,
                canDeliverCommonly = true,
                canPickUp = true,
            )
        }
        productRepository.saveAll(products)
    }
}
