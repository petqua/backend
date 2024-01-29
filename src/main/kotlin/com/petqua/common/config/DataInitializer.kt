package com.petqua.common.config

import com.petqua.domain.announcement.Announcement
import com.petqua.domain.announcement.AnnouncementRepository
import com.petqua.domain.banner.Banner
import com.petqua.domain.banner.BannerRepository
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.recommendation.ProductRecommendation
import com.petqua.domain.recommendation.ProductRecommendationRepository
import com.petqua.domain.store.Store
import com.petqua.domain.store.StoreRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
class DataInitializer(
    private val announcementRepository: AnnouncementRepository,
    private val bannerRepository: BannerRepository,
    private val productRepository: ProductRepository,
    private val recommendationRepository: ProductRecommendationRepository,
    private val storeRepository: StoreRepository,
) {

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun init() {
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
            wishCount = 3,
            reviewCount = 10,
            reviewTotalScore = 50,
            thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg",
            description = "니모를 찾아서 주연"
        )
        val product2 = Product(
            name = "참고등어",
            category = "대형어",
            price = BigDecimal.valueOf(20000L).setScale(2),
            storeId = store1.id,
            discountRate = 10,
            discountPrice = BigDecimal(18000L).setScale(2),
            wishCount = 5,
            reviewCount = 3,
            reviewTotalScore = 15,
            thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail2.jpeg",
            description = "제주산"
        )
        val product3 = Product(
            name = "니모를 찾아서 세트",
            category = "기타과",
            price = BigDecimal.valueOf(80000L).setScale(2),
            storeId = store1.id,
            discountRate = 50,
            discountPrice = BigDecimal(40000L).setScale(2),
            wishCount = 100,
            reviewCount = 50,
            reviewTotalScore = 250,
            thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg",
            description = "니모를 찾아서 주연 조연"
        )
        productRepository.saveAll(listOf(product1, product2, product3))

        // productRecommendation
        val productRecommendation1 = ProductRecommendation(productId = product3.id)
        recommendationRepository.saveAll(listOf(productRecommendation1))
    }
}
