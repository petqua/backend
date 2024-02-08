package com.petqua.presentation.product

import com.petqua.application.product.dto.ProductReviewsResponse
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productReview
import com.petqua.test.fixture.productReviewImage
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import requestReadAllReviewProducts

class ProductReviewControllerTest(
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
) : ApiTestConfig() {
    init {

        val store = storeRepository.save(store(name = "펫쿠아"))
        val member = memberRepository.save(member(nickname = "쿠아"))
        val product = productRepository.save(
            product(
                name = "상품1",
                storeId = store.id,
                discountPrice = BigDecimal.ZERO,
                reviewCount = 0,
                reviewTotalScore = 0
            )
        )

        val savedProductReviews = productReviewRepository.saveAll(
            listOf(
                productReview(
                    productId = product.id,
                    reviewerId = member.id,
                    score = 5,
                    recommendCount = 1,
                    hasPhotos = false,
                ),
                productReview(
                    productId = product.id,
                    reviewerId = member.id,
                    score = 4,
                    recommendCount = 2,
                    hasPhotos = true,
                ),
                productReview(
                    productId = product.id,
                    reviewerId = member.id,
                    score = 3,
                    recommendCount = 3,
                    hasPhotos = false,
                ),
                productReview(
                    productId = product.id,
                    reviewerId = member.id,
                    score = 2,
                    recommendCount = 4,
                    hasPhotos = true,
                ),
                productReview(
                    productId = product.id,
                    reviewerId = member.id,
                    score = 1,
                    recommendCount = 5,
                    hasPhotos = true,
                ),
            )
        )

        val hasPhotoReviewIds = savedProductReviews.filter { it.hasPhotos }.map { it.id } // total 3
        productReviewImageRepository.saveAll(
            listOf(
                productReviewImage(productReviewId = hasPhotoReviewIds[0], imageUrl = "imageUrl1-1"),
                productReviewImage(productReviewId = hasPhotoReviewIds[1], imageUrl = "imageUrl2-1"),
                productReviewImage(productReviewId = hasPhotoReviewIds[1], imageUrl = "imageUrl2-2"),
                productReviewImage(productReviewId = hasPhotoReviewIds[1], imageUrl = "imageUrl2-3"),
                productReviewImage(productReviewId = hasPhotoReviewIds[2], imageUrl = "imageUrl3-1"),
                productReviewImage(productReviewId = hasPhotoReviewIds[2], imageUrl = "imageUrl3-2"),
            )
        )

        Given("조건에 따라 상품 후기를 조회 하면") {

            When("전체 별점, 최신순으로 조회 하면") {

                val response = requestReadAllReviewProducts(productId = product.id, limit = 3)

                Then("조회된 상품 후기 목록을 반환한다") {
                    val responseBody = response.`as`(ProductReviewsResponse::class.java)

                    assertSoftly(responseBody.productReviews) {
                        size shouldBe 3
                        shouldBeSortedWith(compareByDescending { it.createdAt })
                    }
                }
            }

            When("마지막 id의 값을 입력 하면") {
                val response = requestReadAllReviewProducts(productId = product.id, lastViewedId = 3)

                Then("그 이후의 상품 후기 목록을 반환한다") {
                    val responseBody = response.`as`(ProductReviewsResponse::class.java)

                    assertSoftly(responseBody) {
                        productReviews.size shouldBe 2
                        productReviews shouldBeSortedWith compareByDescending { it.createdAt }
                        hasNextPage shouldBe false
                    }
                }
            }

            When("사진만 조회 하면") {
                val response = requestReadAllReviewProducts(productId = product.id, photoOnly = true)

                Then("사진이 있는 상품 후기 목록을 반환한다") {
                    val responseBody = response.`as`(ProductReviewsResponse::class.java)

                    assertSoftly(responseBody.productReviews) {
                        size shouldBe 3
                        shouldBeSortedWith(compareByDescending { it.createdAt })
                        forAll { it.hasPhotos shouldBe true }
                        forAll { it.images.size shouldBeGreaterThanOrEqual 1 }
                    }
                }
            }

            When("별점 3점만 조회 하면") {
                val response = requestReadAllReviewProducts(productId = product.id, score = 3)

                Then("해당 별점의 상품 후기 목록만 반환한다") {
                    val responseBody = response.`as`(ProductReviewsResponse::class.java)

                    assertSoftly(responseBody.productReviews) {
                        size shouldBe 1
                        forAll { it.score shouldBe 3 }
                    }
                }
            }
        }
    }
}
