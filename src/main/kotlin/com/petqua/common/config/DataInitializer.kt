package com.petqua.common.config

import com.petqua.common.domain.Money
import com.petqua.domain.announcement.Announcement
import com.petqua.domain.announcement.AnnouncementRepository
import com.petqua.domain.auth.AuthCredentials
import com.petqua.domain.auth.AuthCredentialsRepository
import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.banner.Banner
import com.petqua.domain.banner.BannerRepository
import com.petqua.domain.fish.Fish
import com.petqua.domain.fish.FishRepository
import com.petqua.domain.keyword.ProductKeyword
import com.petqua.domain.keyword.ProductKeywordRepository
import com.petqua.domain.member.FishLifeYear
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.member.nickname.Nickname
import com.petqua.domain.member.nickname.NicknameWord
import com.petqua.domain.member.nickname.NicknameWordRepository
import com.petqua.domain.notification.Notification
import com.petqua.domain.notification.NotificationRepository
import com.petqua.domain.order.ShippingAddress
import com.petqua.domain.order.ShippingAddressRepository
import com.petqua.domain.policy.bannedword.BannedWord
import com.petqua.domain.policy.bannedword.BannedWordRepository
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSnapshot
import com.petqua.domain.product.ProductSnapshotRepository
import com.petqua.domain.product.WishCount
import com.petqua.domain.product.WishProduct
import com.petqua.domain.product.WishProductRepository
import com.petqua.domain.product.category.Category
import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.category.Family
import com.petqua.domain.product.category.Species
import com.petqua.domain.product.detail.description.ProductDescription
import com.petqua.domain.product.detail.description.ProductDescriptionContent
import com.petqua.domain.product.detail.description.ProductDescriptionRepository
import com.petqua.domain.product.detail.description.ProductDescriptionTitle
import com.petqua.domain.product.detail.image.ImageType.DESCRIPTION
import com.petqua.domain.product.detail.image.ImageType.SAMPLE
import com.petqua.domain.product.detail.image.ProductImage
import com.petqua.domain.product.detail.image.ProductImageRepository
import com.petqua.domain.product.detail.info.DifficultyLevel.NORMAL
import com.petqua.domain.product.detail.info.OptimalTankSize.TANK2
import com.petqua.domain.product.detail.info.OptimalTemperature
import com.petqua.domain.product.detail.info.ProductInfo
import com.petqua.domain.product.detail.info.ProductInfoRepository
import com.petqua.domain.product.detail.info.Temperament.PEACEFUL
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.domain.product.option.Sex.HERMAPHRODITE
import com.petqua.domain.product.option.Sex.MALE
import com.petqua.domain.product.review.ProductReview
import com.petqua.domain.product.review.ProductReviewContent
import com.petqua.domain.product.review.ProductReviewImage
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.product.review.ProductReviewScore
import com.petqua.domain.recommendation.ProductRecommendation
import com.petqua.domain.recommendation.ProductRecommendationRepository
import com.petqua.domain.store.Store
import com.petqua.domain.store.StoreRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.random.Random

@Component
@Profile("local", "prod")
class DataInitializer(
    private val announcementRepository: AnnouncementRepository,
    private val bannerRepository: BannerRepository,
    private val productRepository: ProductRepository,
    private val recommendationRepository: ProductRecommendationRepository,
    private val storeRepository: StoreRepository,
    private val authCredentialsRepository: AuthCredentialsRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
    private val productInfoRepository: ProductInfoRepository,
    private val productImageRepository: ProductImageRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val productSnapshotRepository: ProductSnapshotRepository,
    private val wishProductRepository: WishProductRepository,
    private val productKeywordRepository: ProductKeywordRepository,
    private val productDescriptionRepository: ProductDescriptionRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val fishRepository: FishRepository,
    private val bannedWordRepository: BannedWordRepository,
    private val nicknameWordRepository: NicknameWordRepository,
    private val notificationRepository: NotificationRepository,
) {

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun setUpData() {
        // member
        val authCredentials = saveAuthCredentials()
        saveMember(authCredentials.id)

        // fish
        saveFishes()

        // bannedWord
        saveBannedWords()

        // nicknameWord
        saveNicknameWords()

        // announcement
        saveAnnouncements()

        // banner
        saveBanners()

        // shippingAddress
        saveShippingAddress(authCredentials)

        // others
        saveCommerceData(authCredentials.id)
    }

    private fun saveAuthCredentials(): AuthCredentials {
        return authCredentialsRepository.save(
            AuthCredentials(
                oauthId = 1L,
                oauthServerNumber = 1,
                oauthAccessToken = "xxx.yyy.zzz",
                oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(10000),
                oauthRefreshToken = "xxx.yyy.zzz",
            )
        )
    }

    private fun saveMember(authCredentialsId: Long) {
        memberRepository.save(
            Member(
                authCredentialsId = authCredentialsId,
                authority = MEMBER,
                nickname = Nickname.from("홍길동"),
                profileImageUrl = null,
                fishTankCount = 1,
                fishLifeYear = FishLifeYear.from(1),
                hasAgreedToMarketingNotification = true,
                isDeleted = false,
            )
        )
    }

    private fun saveFishes() {
        fishRepository.saveAll(
            listOf(
                Fish(species = com.petqua.domain.fish.Species.from("구피")),
                Fish(species = com.petqua.domain.fish.Species.from("베타")),
                Fish(species = com.petqua.domain.fish.Species.from("임베리스")),
            )
        )
    }

    private fun saveBannedWords() {
        bannedWordRepository.saveAll(
            listOf(
                BannedWord(word = "씨발"),
                BannedWord(word = "병신"),
            )
        )
    }

    private fun saveNicknameWords() {
        nicknameWordRepository.saveAll(
            listOf(
                NicknameWord(word = "펫쿠아"),
                NicknameWord(word = "물고기"),
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

    private fun saveShippingAddress(authCredentials: AuthCredentials) {
        shippingAddressRepository.save(
            ShippingAddress(
                memberId = authCredentials.id,
                name = "집",
                receiver = "홍길동",
                phoneNumber = "010-1234-5678",
                zipCode = 12345,
                address = "서울시 강남구 역삼동 99번길",
                detailAddress = "101동 101호",
                isDefaultAddress = true,
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
            val safeDeliveryFee = when {
                (it % 4) == 0 -> Money.from(5000L)
                else -> null
            }
            val commonDeliveryFee = when {
                (it % 5) == 0 -> Money.from(3000L)
                else -> null
            }
            val pickUpDeliveryFee = when {
                (it % 2) == 0 -> Money.from(0L)
                else -> null
            }
            val reviewCount = (1..5).random()

            val productInfo = productInfoRepository.save(
                ProductInfo(
                    categoryId = categoryId,
                    optimalTemperature = OptimalTemperature(22, 25),
                    difficultyLevel = NORMAL,
                    optimalTankSize = TANK2,
                    temperament = PEACEFUL
                )
            )

            val productDescriptionId = when {
                (it % 4) != 0 -> productDescriptionRepository.save(
                    ProductDescription(
                        title = ProductDescriptionTitle("물생활 핵 인싸어, 상품$it"),
                        content = ProductDescriptionContent("지느러미가 아름다운 상품$it 입니다")
                    )
                ).id

                else -> null
            }

            Product(
                name = "상품$it",
                categoryId = categoryId,
                price = Money.from(80000L),
                storeId = store.id,
                discountRate = 50,
                discountPrice = Money.from(40000L),
                wishCount = WishCount(100),
                reviewCount = reviewCount,
                reviewTotalScore = (1..reviewCount).sum(),
                thumbnailUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg",
                safeDeliveryFee = safeDeliveryFee,
                commonDeliveryFee = commonDeliveryFee,
                pickUpDeliveryFee = pickUpDeliveryFee,
                productDescriptionId = productDescriptionId,
                productInfoId = productInfo.id,
            )
        }
        productRepository.saveAll(products)
        productSnapshotRepository.saveAll(products.map { ProductSnapshot.from(it) })

        val productOptions = products.map {
            val sex = when {
                (it.id % 3).toInt() == 0 -> MALE
                (it.id % 7).toInt() == 0 -> FEMALE
                else -> HERMAPHRODITE
            }

            ProductOption(
                productId = it.id,
                sex = sex,
                additionalPrice = Money.from(0L),
            )
        }
        productOptionRepository.saveAll(productOptions)

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

        // productImage
        val productImages = products.flatMap { product ->
            List(Random.nextInt(1, 6)) {
                ProductImage(
                    productId = product.id,
                    imageUrl = "https://docs.petqua.co.kr/products/thumbnails/thumbnail3.jpeg",
                    imageType = SAMPLE
                )
            }
        }
        productImageRepository.saveAll(productImages)

        // productDescriptionImage
        val productDescriptionImages = products.map {
            ProductImage(
                productId = it.id,
                imageUrl = "https://www.goldmoonaqua.com/web/upload/NNEditor/20221226/copy-1672038777-guppy_EC958CEBB984EB85B8ED9280EBA088EB939C_02.png",
                imageType = DESCRIPTION
            )
        }
        productImageRepository.saveAll(productDescriptionImages)

        // review
        val productReviews = products.flatMap { product ->
            val hasPhotos = (product.id % 2).toInt() == 0

            (1..product.wishCount.value).map {
                ProductReview(
                    productId = product.id,
                    memberId = memberId,
                    content = ProductReviewContent("진짜 좋아요 ${product.name} 꼭 다시 살 거예요"),
                    score = ProductReviewScore(it % 5 + 1),
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

        // notification
        notificationRepository.saveAll(
            (1..10).map {
                Notification(
                    memberId = memberId,
                    title = "알림$it",
                    content = "알림$it 내용",
                    isRead = it % 2 == 0,
                )
            }
        )
    }
}
