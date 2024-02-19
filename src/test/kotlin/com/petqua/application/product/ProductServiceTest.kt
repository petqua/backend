package com.petqua.application.product

import com.petqua.application.product.dto.ProductKeywordQuery
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductReadQuery
import com.petqua.application.product.dto.ProductSearchQuery
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.domain.auth.token.AccessTokenClaims
import com.petqua.domain.keyword.ProductKeywordRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSourceType.NONE
import com.petqua.domain.product.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.product.WishProductRepository
import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.detail.description.ProductDescriptionRepository
import com.petqua.domain.product.detail.image.ImageType.DESCRIPTION
import com.petqua.domain.product.detail.image.ImageType.SAMPLE
import com.petqua.domain.product.detail.image.ProductImageRepository
import com.petqua.domain.product.detail.info.DifficultyLevel
import com.petqua.domain.product.detail.info.OptimalTankSize
import com.petqua.domain.product.detail.info.OptimalTemperature
import com.petqua.domain.product.detail.info.ProductInfoRepository
import com.petqua.domain.product.detail.info.Temperament
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.category
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productDescription
import com.petqua.test.fixture.productDetailResponse
import com.petqua.test.fixture.productImage
import com.petqua.test.fixture.productInfo
import com.petqua.test.fixture.productKeyword
import com.petqua.test.fixture.productOption
import com.petqua.test.fixture.productResponse
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
    private val categoryRepository: CategoryRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val productDescriptionRepository: ProductDescriptionRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    val store = storeRepository.save(store(name = "store"))

    Given("상품 ID로 상품 상세정보를 조회할 때") {
        val member = memberRepository.save(member())

        val category = categoryRepository.save(
            category(
                family = "난태생과",
                species = "고정구피"
            )
        )
        val productDescription = productDescriptionRepository.save(
            productDescription(
                title = "물생활 핵 인싸어, 레드 브론즈 구피",
                content = "레드 턱시도라고도 불리며 지느러미가 아름다운 구피입니다"
            )
        )
        val productInfo = productInfoRepository.save(
            productInfo(
                categoryId = category.id,
                optimalTemperature = OptimalTemperature(26, 28),
                difficultyLevel = DifficultyLevel.EASY,
                optimalTankSize = OptimalTankSize.TANK1,
                temperament = Temperament.PEACEFUL,
            )
        )
        val productOption = productOptionRepository.save(
            productOption(
                sex = FEMALE,
            )
        )
        val product = productRepository.save(
            product(
                name = "고정구피",
                storeId = store.id,
                categoryId = category.id,
                discountPrice = BigDecimal.ZERO,
                reviewCount = 0,
                reviewTotalScore = 0,
                productOptionId = productOption.id,
                productDescriptionId = productDescription.id,
                productInfoId = productInfo.id,
            )
        )
        val productImage = productImageRepository.save(
            productImage(
                productId = product.id,
                imageUrl = "image.jpeg",
                imageType = SAMPLE
            )
        )
        val productDescriptionImage = productImageRepository.save(
            productImage(
                productId = product.id,
                imageUrl = "image.jpeg",
                imageType = DESCRIPTION
            )
        )
        wishProductRepository.save(
            wishProduct(
                productId = product.id,
                memberId = member.id
            )
        )

        When("회원 정보와 상품 ID를 입력하면") {
            val productDetailResponse = productService.readById(
                loginMemberOrGuest = LoginMemberOrGuest(member.id, member.authority),
                productId = product.id
            )

            Then("회원의 찜 여부와 함께 상품 상세정보를 반환한다") {
                productDetailResponse shouldBe productDetailResponse(
                    product = product,
                    storeName = store.name,
                    imageUrls = listOf(productImage.imageUrl),
                    productDescription = productDescription,
                    descriptionImageUrls = listOf(productDescriptionImage.imageUrl),
                    productInfo = productInfo,
                    category = category,
                    hasDistinctSex = productOption.hasDistinctSex(),
                    isWished = true
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
                productId = product.id
            )

            Then("찜 여부는 false 로 상품 상세정보를 반환한다") {
                productDetailResponse shouldBe productDetailResponse(
                    product = product,
                    storeName = store.name,
                    imageUrls = listOf(productImage.imageUrl),
                    productDescription = productDescription,
                    descriptionImageUrls = listOf(productDescriptionImage.imageUrl),
                    productInfo = productInfo,
                    category = category,
                    hasDistinctSex = productOption.hasDistinctSex(),
                    isWished = false
                )
            }
        }
    }

    Given("조건에 따라 상품을 조회할 때") {
        val member = memberRepository.save(member())

        val product1 = productRepository.save(product(storeId = store.id))
        val product2 = productRepository.save(product(storeId = store.id))

        wishProductRepository.save(
            wishProduct(
                productId = product1.id,
                memberId = member.id
            )
        )

        When("회원이 상품을 조회하면") {
            val productsResponse = productService.readAll(
                ProductReadQuery(
                    sourceType = NONE,
                    sorter = ENROLLMENT_DATE_DESC,
                    limit = 2,
                    loginMemberOrGuest = LoginMemberOrGuest.getMemberFrom(AccessTokenClaims(member.id, MEMBER))
                )
            )

            Then("상품의 찜 여부와 함께 상품 정보가 반환된다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        productResponse(
                            product = product2,
                            storeName = store.name,
                            isWished = false,
                        ),
                        productResponse(
                            product = product1,
                            storeName = store.name,
                            isWished = true,
                        )
                    ),
                    hasNextPage = false,
                    totalProductsCount = 2
                )
            }
        }

        When("비회원이 상품을 조회하면") {
            val productsResponse = productService.readAll(
                ProductReadQuery(
                    sourceType = NONE,
                    sorter = ENROLLMENT_DATE_DESC,
                    limit = 2,
                    loginMemberOrGuest = LoginMemberOrGuest.getGuest()
                )
            )

            Then("상품의 찜 여부는 false로 상품 정보가 반환된다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        productResponse(
                            product = product2,
                            storeName = store.name,
                            isWished = false,
                        ),
                        productResponse(
                            product = product1,
                            storeName = store.name,
                            isWished = false,
                        )
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

        val member = memberRepository.save(member())

        wishProductRepository.save(wishProduct(productId = product1.id, memberId = member.id))

        When("비회원이 입력한 검색어가 상품 키워드에 속하면") {
            val query = ProductSearchQuery(
                word = "구피",
                loginMemberOrGuest = LoginMemberOrGuest.getGuest(),
            )

            val productsResponse = productService.readBySearch(query)

            Then("상품 키워드와 연관된 상품을 조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        productResponse(product2, store.name, isWished = false),
                        productResponse(product1, store.name, isWished = false),
                    ),
                    hasNextPage = false,
                    totalProductsCount = 2
                )
            }
        }

        When("비회원이 입력한 검색어가 상품 키워드에 속하지 않으면") {
            val query = ProductSearchQuery(
                word = "고등",
                loginMemberOrGuest = LoginMemberOrGuest.getGuest(),
            )

            val productsResponse = productService.readBySearch(query)

            Then("상품 이름과 연관된 상품을 조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        productResponse(product3, store.name, isWished = false),
                    ),
                    hasNextPage = false,
                    totalProductsCount = 1
                )
            }
        }

        When("회원이 입력한 검색어가 상품 키워드에 속하면") {
            val query = ProductSearchQuery(
                word = "구피",
                loginMemberOrGuest = LoginMemberOrGuest.getMemberFrom(AccessTokenClaims(member.id, MEMBER)),
            )

            val productsResponse = productService.readBySearch(query)

            Then("상품 키워드와 연관된 상품을 조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        productResponse(product2, store.name, isWished = false),
                        productResponse(product1, store.name, isWished = true),
                    ),
                    hasNextPage = false,
                    totalProductsCount = 2
                )
            }
        }

        When("회원이 입력한 검색어가 상품 키워드에 속하지 않으면") {
            val query = ProductSearchQuery(
                word = "고등",
                loginMemberOrGuest = LoginMemberOrGuest.getMemberFrom(AccessTokenClaims(member.id, MEMBER)),
            )

            val productsResponse = productService.readBySearch(query)

            Then("상품 이름과 연관된 상품을 조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        productResponse(product3, store.name, isWished = false),
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
