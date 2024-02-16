package com.petqua.common.config

import com.petqua.domain.announcement.Announcement
import com.petqua.domain.announcement.AnnouncementRepository
import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.banner.Banner
import com.petqua.domain.banner.BannerRepository
import com.petqua.domain.keyword.ProductKeyword
import com.petqua.domain.keyword.ProductKeywordRepository
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.WishCount
import com.petqua.domain.product.WishProduct
import com.petqua.domain.product.WishProductRepository
import com.petqua.domain.product.category.Category
import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.category.Family
import com.petqua.domain.product.category.Species
import com.petqua.domain.product.detail.DifficultyLevel.NORMAL
import com.petqua.domain.product.detail.OptimalTankSize.TANK2
import com.petqua.domain.product.detail.OptimalTemperature
import com.petqua.domain.product.detail.ProductImage
import com.petqua.domain.product.detail.ProductImageRepository
import com.petqua.domain.product.detail.ProductInfo
import com.petqua.domain.product.detail.ProductInfoRepository
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
    private val categoryRepository: CategoryRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
    private val productInfoRepository: ProductInfoRepository,
    private val productImageRepository: ProductImageRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val wishProductRepository: WishProductRepository,
    private val productKeywordRepository: ProductKeywordRepository,
) {

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun setUpData() {
        // member
        val member = saveMember()

        // announcement
        saveAnnouncements()

        // banner
        saveBanners()

        // others
        saveCommerceData(member.id)
    }

    private fun saveMember(): Member {
        return memberRepository.save(
            Member(
                oauthId = "oauthId",
                oauthServerNumber = 1,
                authority = MEMBER,
            )
        )
    }

    private fun saveAnnouncements() {
        announcementRepository.saveAll(
            listOf(
                Announcement(
                    title = "[공지] 펫쿠아 프론트엔드 개발자 구인 중!",
                    linkUrl = "https://team.petqua.co.kr/"
                ),
                Announcement(
                    title = "[공지] 펫쿠아 복지 추가 GPT4 지원 예정",
                    linkUrl = "https://team.petqua.co.kr/"
                ),
                Announcement(
                    title = "[공지] 펫쿠아 개발팀 copilot 적극 활용",
                    linkUrl = "https://team.petqua.co.kr/"
                ),
            )
        )
    }

    private fun saveBanners() {
        bannerRepository.saveAll(
            listOf(
                Banner(
                    imageUrl = "https://docs.petqua.co.kr/banners/b08f14d5ac00721b.jpg",
                    linkUrl = "https://team.petqua.co.kr/"
                ),
                Banner(
                    imageUrl = "https://docs.petqua.co.kr/banners/announcement1.jpg",
                    linkUrl = "https://team.petqua.co.kr/"
                ),
                Banner(
                    imageUrl = "https://docs.petqua.co.kr/banners/announcement2.jpg",
                    linkUrl = "https://team.petqua.co.kr/"
                ),
                Banner(
                    imageUrl = "https://docs.petqua.co.kr/banners/announcement3.jpg",
                    linkUrl = "https://team.petqua.co.kr/"
                ),
            )
        )
    }

    private fun saveCommerceData(memberId: Long) {
        // store
        val store = Store(name = "상점1")
        storeRepository.saveAll(listOf(store))

        // category
        val category1 = Category(family = Family("송사리과"), species = Species("고정구피"))
        val category2 = Category(family = Family("송사리과"), species = Species("팬시구피"))
        categoryRepository.saveAll(listOf(category1, category2))

        // products
        val products = (1..100).map {
            val categoryId = when {
                (it % 2) == 0 -> category1.id
                else -> category2.id
            }
            val canDeliverSafely = when {
                (it % 4) == 0 -> true
                else -> false
            }
            val canDeliverCommonly = when {
                (it % 5) == 0 -> true
                else -> false
            }
            val canPickUp = when {
                (it % 2) == 0 -> true
                else -> false
            }
            val reviewCount = (1..5).random()

            Product(
                name = "상품$it",
                categoryId = categoryId,
                price = BigDecimal.valueOf(80000L).setScale(2),
                storeId = store.id,
                discountRate = 50,
                discountPrice = BigDecimal(40000L).setScale(2),
                wishCount = WishCount(100),
                reviewCount = reviewCount,
                reviewTotalScore = (1..reviewCount).sum(),
                thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg",
                description = "https://www.goldmoonaqua.com/web/upload/NNEditor/20221226/copy-1672038777-guppy_EC958CEBB984EB85B8ED9280EBA088EB939C_02.png",
                canDeliverSafely = canDeliverSafely,
                canDeliverCommonly = canDeliverCommonly,
                canPickUp = canPickUp,
            )
        }
        productRepository.saveAll(products)

        // wishProducts
        val wishProducts = products.filter {
            (it.id % 11).toInt() == 0
        }.map {
            WishProduct(
                productId = it.id,
                memberId = memberId
            )
        }
        wishProductRepository.saveAll(wishProducts)

        // productKeyword
        val productKeywords = products.filter {
            (it.id % 9).toInt() == 0
        }.map {
            ProductKeyword(
                productId = it.id,
                word = "상품"
            )
        }
        productKeywordRepository.saveAll(productKeywords)

        // productRecommendation
        val productRecommendations = products.filter {
            (it.id % 10).toInt() == 0
        }.map {
            ProductRecommendation(productId = it.id)
        }
        recommendationRepository.saveAll(productRecommendations)

        // productOption
        val productOptions = products.map {
            val sex = when {
                (it.id % 3).toInt() == 0 -> MALE
                (it.id % 7).toInt() == 0 -> HERMAPHRODITE
                else -> FEMALE
            }
            ProductOption(
                productId = it.id,
                sex = sex,
                additionalPrice = BigDecimal.ZERO
            )
        }
        productOptionRepository.saveAll(productOptions)

        // productInfo
        val productInfos = products.map {
            ProductInfo(
                productId = it.id,
                categoryId = it.categoryId,
                optimalTemperature = OptimalTemperature(22, 25),
                difficultyLevel = NORMAL,
                optimalTankSize = TANK2,
                temperament = PEACEFUL
            )
        }
        productInfoRepository.saveAll(productInfos)

        // productImage
        val productImages = products.map {
            ProductImage(
                productId = it.id,
                imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg"
            )
        }
        productImageRepository.saveAll(productImages)

        // review
        val productReviews = products.flatMap { product ->
            val hasPhotos = (product.id % 2).toInt() == 0

            (1..product.wishCount.value).map {
                ProductReview(
                    productId = product.id,
                    memberId = memberId,
                    content = "좋아요 ${product.name}",
                    score = it % 5 + 1,
                    hasPhotos = hasPhotos
                )
            }
        }
        productReviewRepository.saveAll(productReviews)

        // reviewImages
        val productReviewImages = productReviews.filter {
            it.hasPhotos
        }.map {
            ProductReviewImage(
                imageUrl = "https://docs.petqua.co.kr/banners/announcement1.jpg",
                productReviewId = it.id
            )
        }
        productReviewImageRepository.saveAll(productReviewImages)
    }
}
