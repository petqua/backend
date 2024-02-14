package com.petqua.application.product

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordQuery
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductReadQuery
import com.petqua.application.product.dto.ProductSearchQuery
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.domain.keyword.ProductKeywordRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSourceType.NONE
import com.petqua.domain.product.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.product.WishProductRepository
import com.petqua.domain.product.detail.DifficultyLevel
import com.petqua.domain.product.detail.OptimalTankSizeLiter
import com.petqua.domain.product.detail.OptimalTemperature
import com.petqua.domain.product.detail.ProductImageRepository
import com.petqua.domain.product.detail.ProductInfoRepository
import com.petqua.domain.product.detail.Temperament
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productImage
import com.petqua.test.fixture.productInfo
import com.petqua.test.fixture.productKeyword
import com.petqua.test.fixture.store
import com.petqua.test.fixture.wishProduct
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import java.math.BigDecimal
import kotlin.Long.Companion.MIN_VALUE

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ProductServiceTest(
    private val productService: ProductService,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productKeywordRepository: ProductKeywordRepository,
    private val productInfoRepository: ProductInfoRepository,
    private val productImageRepository: ProductImageRepository,
    private val memberRepository: MemberRepository,
    private val wishProductRepository: WishProductRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    val store = storeRepository.save(store(name = "store"))

    Given("상품 ID로 상품 상세정보를 조회할 때") {
        val member = memberRepository.save(member())

        val wishedProduct = productRepository.save(
            product(
                name = "고정구피",
                storeId = store.id,
                discountPrice = BigDecimal.ZERO,
                reviewCount = 0,
                reviewTotalScore = 0
            )
        )
        val productInfo = productInfoRepository.save(
            productInfo(
                productId = wishedProduct.id,
                categoryId = 0,
                optimalTemperature = OptimalTemperature(26, 28),
                difficultyLevel = DifficultyLevel.EASY,
                optimalTankSizeLiter = OptimalTankSizeLiter(10, 50),
                temperament = Temperament.PEACEFUL,
            )
        )
        val productImage = productImageRepository.save(
            productImage(
                productId = wishedProduct.id,
                imageUrl = "image.jpeg"
            )
        )
        wishProductRepository.save(
            wishProduct(
                productId = wishedProduct.id,
                memberId = member.id
            )
        )

        When("회원 정보와 상품 ID를 입력하면") {
            val productDetailResponse = productService.readById(
                loginMemberOrGuest = LoginMemberOrGuest(member.id, member.authority),
                productId = wishedProduct.id
            )

            Then("상품 상세정보를 반환한다") {
                productDetailResponse shouldBe ProductDetailResponse(
                    id = wishedProduct.id,
                    name = wishedProduct.name,
                    family = "family",
                    species = "species",
                    price = wishedProduct.price.intValueExact(),
                    storeName = store.name,
                    discountRate = wishedProduct.discountRate,
                    discountPrice = wishedProduct.discountPrice.intValueExact(),
                    wishCount = wishedProduct.wishCount.value,
                    reviewCount = wishedProduct.reviewCount,
                    reviewAverageScore = wishedProduct.averageReviewScore(),
                    thumbnailUrl = wishedProduct.thumbnailUrl,
                    imageUrls = listOf(productImage.imageUrl),
                    canDeliverSafely = wishedProduct.canDeliverSafely,
                    canDeliverCommonly = wishedProduct.canDeliverCommonly,
                    canPickUp = wishedProduct.canPickUp,
                    description = wishedProduct.description,
                    optimalTemperatureMin = productInfo.optimalTemperature.optimalTemperatureMin,
                    optimalTemperatureMax = productInfo.optimalTemperature.optimalTemperatureMax,
                    difficultyLevel = productInfo.difficultyLevel.description,
                    optimalTankSizeMin = productInfo.optimalTankSizeLiter.optimalTankSizeLiterMin,
                    optimalTankSizeMax = productInfo.optimalTankSizeLiter.optimalTankSizeLiterMax,
                    temperament = productInfo.temperament.description,
                    isWished = true,
                )
            }
        }

        When("회원 정보와 존재하지 않는 상품 ID를 입력하면") {
            val loginMember = LoginMemberOrGuest(member.id, member.authority)
            val invalidId = MIN_VALUE

            Then("예외를 던진다") {
                shouldThrow<ProductException> {
                    productService.readById(
                        loginMemberOrGuest = loginMember,
                        productId = invalidId
                    )
                }.exceptionType() shouldBe NOT_FOUND_PRODUCT
            }
        }

        When("비회원 정보와 상품 ID를 입력하면") {
            val productDetailResponse = productService.readById(
                loginMemberOrGuest = LoginMemberOrGuest.getGuest(),
                productId = wishedProduct.id
            )

            Then("상품 상세정보를 반환한다") {
                productDetailResponse shouldBe ProductDetailResponse(
                    id = wishedProduct.id,
                    name = wishedProduct.name,
                    family = "family",
                    species = "species",
                    price = wishedProduct.price.intValueExact(),
                    storeName = store.name,
                    discountRate = wishedProduct.discountRate,
                    discountPrice = wishedProduct.discountPrice.intValueExact(),
                    wishCount = wishedProduct.wishCount.value,
                    reviewCount = wishedProduct.reviewCount,
                    reviewAverageScore = wishedProduct.averageReviewScore(),
                    thumbnailUrl = wishedProduct.thumbnailUrl,
                    imageUrls = listOf(productImage.imageUrl),
                    canDeliverSafely = wishedProduct.canDeliverSafely,
                    canDeliverCommonly = wishedProduct.canDeliverCommonly,
                    canPickUp = wishedProduct.canPickUp,
                    description = wishedProduct.description,
                    optimalTemperatureMin = productInfo.optimalTemperature.optimalTemperatureMin,
                    optimalTemperatureMax = productInfo.optimalTemperature.optimalTemperatureMax,
                    difficultyLevel = productInfo.difficultyLevel.description,
                    optimalTankSizeMin = productInfo.optimalTankSizeLiter.optimalTankSizeLiterMin,
                    optimalTankSizeMax = productInfo.optimalTankSizeLiter.optimalTankSizeLiterMax,
                    temperament = productInfo.temperament.description,
                    isWished = false,
                )
            }
        }
    }

    Given("조건에 따라") {
        val product1 = productRepository.save(product(storeId = store.id))
        val product2 = productRepository.save(product(storeId = store.id))

        val query = ProductReadQuery(
            sourceType = NONE,
            sorter = ENROLLMENT_DATE_DESC,
            limit = 2
        )

        When("상품을") {
            val productsResponse = productService.readAll(query)

            Then("조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        ProductResponse(product2, store.name),
                        ProductResponse(product1, store.name),
                    ),
                    hasNextPage = false,
                    totalProductsCount = 2
                )
            }
        }
    }

    Given("상품 검색창에서 추천 검색어 기능을 이용할 때") {
        val product1 = productRepository.save(product(name = "블루네온 구피", storeId = store.id))
        val product2 = productRepository.save(product(name = "레드턱시도 구피", storeId = store.id))
        val product3 = productRepository.save(product(name = "고등어", storeId = store.id))

        productKeywordRepository.save(productKeyword(word = "구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "블루네온 구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "구피", productId = product2.id))
        productKeywordRepository.save(productKeyword(word = "레드턱시도 구피", productId = product2.id))

        val query = ProductKeywordQuery(word = "구피")

        When("검색어를 입력하면") {
            val productKeywordResponses = productService.readAutoCompleteKeywords(query)

            Then("상품 키워드 목록이 문자 길이 오름차순으로 반환된다") {
                productKeywordResponses shouldBe listOf(
                    ProductKeywordResponse("구피"),
                    ProductKeywordResponse("블루네온 구피"),
                    ProductKeywordResponse("레드턱시도 구피"),
                )
            }
        }
    }

    Given("검색으로 상품을 조회할 때") {
        val product1 = productRepository.save(product(name = "블루네온 구피", storeId = store.id))
        val product2 = productRepository.save(product(name = "레드턱시도 구피", storeId = store.id))
        val product3 = productRepository.save(product(name = "고등어", storeId = store.id))

        productKeywordRepository.save(productKeyword(word = "구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "블루네온 구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "구피", productId = product2.id))
        productKeywordRepository.save(productKeyword(word = "레드턱시도 구피", productId = product2.id))

        When("검색어가 상품 키워드에 속하면") {
            val query = ProductSearchQuery(word = "구피")

            val productsResponse = productService.readBySearch(query)

            Then("상품 키워드와 연관된 상품을 조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        ProductResponse(product2, store.name),
                        ProductResponse(product1, store.name),
                    ),
                    hasNextPage = false,
                    totalProductsCount = 2
                )
            }
        }

        When("검색어가 상품 키워드에 속하지 않으면") {
            val query = ProductSearchQuery(word = "고등")

            val productsResponse = productService.readBySearch(query)

            Then("상품 이름과 연관된 상품을 조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        ProductResponse(product3, store.name),
                    ),
                    hasNextPage = false,
                    totalProductsCount = 1
                )
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
