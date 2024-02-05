package com.petqua.presentation.product

import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.WishProduct
import com.petqua.domain.product.WishProductRepository
import com.petqua.domain.store.Store
import com.petqua.domain.store.StoreRepository
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import io.kotest.matchers.shouldBe
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import java.math.BigDecimal

class WishProductControllerTest(
    wishProductRepository: WishProductRepository,
    productRepository: ProductRepository,
    storeRepository: StoreRepository,
) : ApiTestConfig() {

    init {
        Given("찜 상품 수정을") {
            val accessToken = signInAsMember().accessToken
            val product = productRepository.save(product())
            val request = UpdateWishRequest(
                productId = product.id
            )

            When("요청하면") {
                val response = Given {
                    log().all()
                        .body(request)
                        .auth().preemptive().oauth2(accessToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                } When {
                    post("/products/wishes")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("찜 목록에 상품이 추가되거나 제거된다") {
                    response.statusCode shouldBe NO_CONTENT.value()
                }
            }
        }

        Given("찜 목록 조회를") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)
            val store = storeRepository.save(store())
            val (product1, product2, product3) = saveProducts(productRepository, store)
            wishProductRepository.save(
                WishProduct(
                    productId = product1.id,
                    memberId = memberId
                )
            )
            wishProductRepository.save(
                WishProduct(
                    productId = product2.id,
                    memberId = memberId
                )
            )
            wishProductRepository.save(
                WishProduct(
                    productId = product3.id,
                    memberId = memberId
                )
            )

            When("요청하면") {
                val responses = Given {
                    log().all()
                        .param("lastViewedId", -1L)
                        .param("limit", 20)
                        .auth().preemptive().oauth2(accessToken)
                } When {
                    get("/products/wishes")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("찜 상품들이 반환된다") {
                    val wishProductsResponse = responses.`as`(WishProductsResponse::class.java)

                    responses.statusCode shouldBe OK.value()
                    wishProductsResponse.totalWishProductsCount shouldBe 3
                    wishProductsResponse.wishProducts.size shouldBe 3
                    wishProductsResponse.hasNextPage shouldBe false
                }
            }
        }
    }

    private fun saveProducts(
        productRepository: ProductRepository,
        store: Store
    ): Triple<Product, Product, Product> {
        val product1 = productRepository.save(
            product(
                name = "상품1",
                storeId = store.id,
                discountPrice = BigDecimal.ZERO,
                reviewCount = 0,
                reviewTotalScore = 0
            )
        )
        val product2 = productRepository.save(
            product(
                name = "상품2",
                storeId = store.id,
                discountPrice = BigDecimal.ONE,
                reviewCount = 1,
                reviewTotalScore = 1
            )
        )
        val product3 = productRepository.save(
            product(
                name = "상품3",
                storeId = store.id,
                discountPrice = BigDecimal.ONE,
                reviewCount = 1,
                reviewTotalScore = 5
            )
        )
        return Triple(product1, product2, product3)
    }
}
