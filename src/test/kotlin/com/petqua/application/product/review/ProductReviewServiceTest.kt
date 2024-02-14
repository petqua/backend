package com.petqua.application.product.review

import com.petqua.application.product.dto.ProductReviewReadQuery
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.product.review.ProductReviewSorter.RECOMMEND_DESC
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import com.petqua.domain.store.StoreRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productReview
import com.petqua.test.fixture.productReviewImage
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class ProductReviewServiceTest(
    private val productReviewService: ProductReviewService,
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

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

    Given("상품 후기를 조건에 따라") {

        When("전체 별점, 최신순으로 조회 하면") {
            val query = ProductReviewReadQuery(
                productId = product.id,
                memberId = member.id,
                sorter = REVIEW_DATE_DESC,
                score = null,
                limit = 3,
            )
            val response = productReviewService.readAll(query)

            Then("조건에 맞는 상품 후기 목록을 반환한다") {
                assertSoftly(response.productReviews) {
                    size shouldBe 3
                    shouldBeSortedWith(compareByDescending { it.createdAt })
                    response.hasNextPage shouldBe true
                }
            }
        }

        When("마지막 id의 값을 입력 하면") {
            val query = ProductReviewReadQuery(
                productId = product.id,
                memberId = member.id,
                lastViewedId = savedProductReviews[2].id,  // 5개의 상품 후기 중 3번째 후기의 id
                limit = 4,
            )
            val response = productReviewService.readAll(query)

            Then("조회된 상품 후기 목록을 반환한다") {
                assertSoftly(response.productReviews) {
                    size shouldBe 2
                    shouldBeSortedWith(compareByDescending { it.createdAt })
                    response.hasNextPage shouldBe false
                }
            }
        }

        When("사진이 있는 후기만 조회 하면") {
            val query = ProductReviewReadQuery(
                productId = product.id,
                memberId = member.id,
                photoOnly = true,
                limit = 3,
            )
            val response = productReviewService.readAll(query)

            Then("사진이 포함된 상품 후기 목록만 반환한다") {
                assertSoftly(response.productReviews) {
                    size shouldBe 3
                    shouldBeSortedWith(compareByDescending { it.createdAt })
                    response.hasNextPage shouldBe false
                    forAll { it.hasPhotos shouldBe true }
                    forAll { it.images.size shouldBeGreaterThanOrEqual 1 }
                }
            }
        }

        When("별점과 정렬 조건을 입력 하면") {
            val query = ProductReviewReadQuery(
                productId = product.id,
                memberId = member.id,
                sorter = RECOMMEND_DESC,
                score = 3,
                limit = 3,
            )
            val response = productReviewService.readAll(query)

            Then("해당 별점의 상품 후기들이 정렬 조건으로 정렬 되어 반환한다") {
                assertSoftly(response.productReviews) {
                    size shouldBe 1
                    shouldBeSortedWith(compareByDescending { it.recommendCount })
                    forAll { it.score shouldBe 3 }
                }
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
