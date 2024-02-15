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
import com.petqua.domain.product.category.Category
import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.category.Family
import com.petqua.domain.product.category.Species
import com.petqua.domain.product.detail.DifficultyLevel.EASY
import com.petqua.domain.product.detail.DifficultyLevel.HARD
import com.petqua.domain.product.detail.DifficultyLevel.NORMAL
import com.petqua.domain.product.detail.OptimalTankSize.TANK1
import com.petqua.domain.product.detail.OptimalTankSize.TANK2
import com.petqua.domain.product.detail.OptimalTankSize.TANK3
import com.petqua.domain.product.detail.OptimalTemperature
import com.petqua.domain.product.detail.ProductImage
import com.petqua.domain.product.detail.ProductImageRepository
import com.petqua.domain.product.detail.ProductInfo
import com.petqua.domain.product.detail.ProductInfoRepository
import com.petqua.domain.product.detail.Temperament.AGGRESSIVE
import com.petqua.domain.product.detail.Temperament.PEACEFUL
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.domain.product.option.Sex.HERMAPHRODITE
import com.petqua.domain.product.option.Sex.MALE
import com.petqua.domain.product.review.ProductReview
import com.petqua.domain.product.review.ProductReviewImage
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.recommendation.ProductRecommendation
import com.petqua.domain.recommendation.ProductRecommendationRepository
import com.petqua.domain.store.Store
import com.petqua.domain.store.StoreRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
@Profile("local", "prod")
class DataInitializer(
    private val announcementRepository: AnnouncementRepository,
    private val bannerRepository: BannerRepository,
    private val productRepository: ProductRepository,
    private val recommendationRepository: ProductRecommendationRepository,
    private val storeRepository: StoreRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
    private val productInfoRepository: ProductInfoRepository,
    private val productImageRepository: ProductImageRepository,
    private val productOptionRepository: ProductOptionRepository,
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

        // category
        val category1 = Category(family = Family("송사리과"), species = Species("고정구피"))
        val category2 = Category(family = Family("송사리과"), species = Species("팬시구피"))
        categoryRepository.saveAll(listOf(category1, category2))

        // product
        val product1 = Product(
            name = "니모",
            categoryId = category1.id,
            price = BigDecimal.valueOf(30000L).setScale(2),
            storeId = store1.id,
            discountRate = 10,
            discountPrice = BigDecimal(27000L).setScale(2),
            wishCount = WishCount(3),
            reviewCount = 10,
            reviewTotalScore = 50,
            thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg",
            description = "https://www.goldmoonaqua.com/web/upload/NNEditor/20221226/copy-1672038777-guppy_EC958CEBB984EB85B8ED9280EBA088EB939C_02.png",
            canDeliverSafely = false,
            canDeliverCommonly = true,
            canPickUp = false,
        )
        val product2 = Product(
            name = "참고등어",
            categoryId = category2.id,
            price = BigDecimal.valueOf(20000L).setScale(2),
            storeId = store1.id,
            discountRate = 10,
            discountPrice = BigDecimal(18000L).setScale(2),
            wishCount = WishCount(5),
            reviewCount = 3,
            reviewTotalScore = 15,
            thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail2.jpeg",
            description = "https://shop-phinf.pstatic.net/20231116_59/1700105133758mQSba_JPEG/%EC%B9%A8%EC%B0%A9%ED%95%9C%ED%9B%84%EB%93%9C_%EC%83%81%EC%84%B8_1.jpg?type=w860",
            canDeliverSafely = true,
            canDeliverCommonly = false,
            canPickUp = true,
        )
        val product3 = Product(
            name = "니모를 찾아서 세트",
            categoryId = category1.id,
            price = BigDecimal.valueOf(80000L).setScale(2),
            storeId = store1.id,
            discountRate = 50,
            discountPrice = BigDecimal(40000L).setScale(2),
            wishCount = WishCount(100),
            reviewCount = 50,
            reviewTotalScore = 250,
            thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg",
            description = "https://store.img11.co.kr/68636870/cf26fe1a-98d5-486b-a79a-9dbfade97fb6_1690335864752.jpg",
            canDeliverSafely = false,
            canDeliverCommonly = true,
            canPickUp = true,
        )
        productRepository.saveAll(listOf(product1, product2, product3))
        saveProducts(store1.id, category1.id)

        // productRecommendation
        val productRecommendation1 = ProductRecommendation(productId = product3.id)
        recommendationRepository.saveAll(listOf(productRecommendation1))

        // productOption
        productOptionRepository.saveAll(
            listOf(
                ProductOption(
                    productId = product1.id,
                    sex = MALE,
                    additionalPrice = BigDecimal.ZERO
                ),
                ProductOption(
                    productId = product2.id,
                    sex = FEMALE,
                    additionalPrice = BigDecimal.ZERO
                ),
                ProductOption(
                    productId = product3.id,
                    sex = HERMAPHRODITE,
                    additionalPrice = BigDecimal.ZERO
                ),
            )
        )

        //productImage
        productImageRepository.saveAll(
            listOf(
                ProductImage(
                    productId = product1.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg"
                ),
                ProductImage(
                    productId = product1.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg"
                ),
                ProductImage(
                    productId = product1.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg"
                ),
                ProductImage(
                    productId = product1.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg"
                ),
                ProductImage(
                    productId = product1.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg"
                ),
                ProductImage(
                    productId = product2.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail2.jpeg"
                ),
                ProductImage(
                    productId = product2.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail2.jpeg"
                ),
                ProductImage(
                    productId = product2.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail2.jpeg"
                ),
                ProductImage(
                    productId = product3.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg"
                ),
                ProductImage(
                    productId = product3.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg"
                ),
            )
        )

        // productInfo
        productInfoRepository.saveAll(
            listOf(
                ProductInfo(
                    productId = product1.id,
                    categoryId = category1.id,
                    optimalTemperature = OptimalTemperature(22, 25),
                    difficultyLevel = EASY,
                    optimalTankSize = TANK1,
                    temperament = AGGRESSIVE
                ),
                ProductInfo(
                    productId = product2.id,
                    categoryId = category2.id,
                    optimalTemperature = OptimalTemperature(22, 25),
                    difficultyLevel = HARD,
                    optimalTankSize = TANK3,
                    temperament = PEACEFUL
                ),
                ProductInfo(
                    productId = product3.id,
                    categoryId = category2.id,
                    optimalTemperature = OptimalTemperature(22, 25),
                    difficultyLevel = NORMAL,
                    optimalTankSize = TANK2,
                    temperament = PEACEFUL
                )
            )
        )

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

    private fun saveProducts(storeId: Long, categoryId: Long) {
        val products = (1..100).map {
            Product(
                name = "니모를 찾아서 세트$it",
                categoryId = categoryId,
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
