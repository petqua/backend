package com.petqua.domain.product.review

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.dto.ProductReviewReadCondition
import com.petqua.domain.product.review.ProductReviewSorter.RECOMMEND_DESC
import com.petqua.domain.store.StoreRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productReview
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductReviewCustomRepositoryImplTest(
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("조건에 따라 상품 후기를 조회 할 때") {
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

        productReviewRepository.saveAll(
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

        When("조건 없이 기본 값으로 전체 조회 하면") {
            val productReviewResponse = productReviewRepository.findAllByCondition(
                condition = ProductReviewReadCondition(productId = product.id, photoOnly = false),
                paging = CursorBasedPaging(limit = 3),
            )

            Then("조회된 상품 후기 목록을 반환한다") {
                assertSoftly(productReviewResponse) {
                    size shouldBe 3
                    shouldBeSortedWith(compareByDescending { it.createdAt })
                }
            }
        }

        When("마지막 id의 값을 입력 하면") {
            val productReviewResponse = productReviewRepository.findAllByCondition(
                condition = ProductReviewReadCondition(productId = product.id, photoOnly = false),
                paging = CursorBasedPaging(lastViewedId = 3L, limit = 3), // id가 5,4,3,2,1 인 리뷰중 [2, 1] 만 조회 된다.
            )

            Then("해당 id 이후의 상품 후기 목록을 반환한다") {
                assertSoftly(productReviewResponse) {
                    size shouldBe 2
                    shouldBeSortedWith(compareByDescending { it.createdAt })
                }
            }
        }

        When("정렬 기준을 입력 하면") {
            val productReviewResponse = productReviewRepository.findAllByCondition(
                condition = ProductReviewReadCondition(
                    productId = product.id,
                    photoOnly = false,
                    sorter = RECOMMEND_DESC
                ),
                paging = CursorBasedPaging(limit = 3),
            )

            Then("해당 기준으로 정렬된 상품 후기를 반환 한다") {
                assertSoftly(productReviewResponse) {
                    size shouldBe 3
                    shouldBeSortedWith(compareByDescending { it.recommendCount })
                }
            }
        }

        When("사진 리뷰 필터를 추가 하면") {
            val productReviewResponse = productReviewRepository.findAllByCondition(
                condition = ProductReviewReadCondition(
                    productId = product.id,
                    photoOnly = true,
                    sorter = RECOMMEND_DESC
                ),
                paging = CursorBasedPaging(limit = 3),
            )

            Then("사진 리뷰만 조회 된다") {
                productReviewResponse.forAll { it.hasPhotos shouldBe true }
            }
        }

        When("사진 리뷰 필터와 점수 필터를 추가 하면") {
            val productReviewResponse = productReviewRepository.findAllByCondition(
                condition = ProductReviewReadCondition(
                    productId = product.id,
                    photoOnly = true,
                    sorter = RECOMMEND_DESC,
                    score = 3,
                ),
                paging = CursorBasedPaging(limit = 3),
            )

            Then("점수 필터가 적용된 사진 리뷰가 조회된다") {
                productReviewResponse.forAll {
                    it.hasPhotos shouldBe true
                    it.score shouldBe 3
                }
            }
        }
    }

    Given("상품 후기의 점수별 개수를 조회 할 때") {
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

        productReviewRepository.saveAll(
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
                    score = 5,
                    recommendCount = 2,
                    hasPhotos = true,
                ),
                productReview(
                    productId = product.id,
                    reviewerId = member.id,
                    score = 5,
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
                    score = 2,
                    recommendCount = 5,
                    hasPhotos = true,
                ),
            )
        )

        When("상품 후기의 점수별 개수를 조회 하면") {
            val productReviewScoreWithCount = productReviewRepository.findReviewScoresWithCount(product.id)

            Then("점수별 개수를 반환 한다") {
                assertSoftly(productReviewScoreWithCount) {
                    size shouldBe 2
                    find { it.score == 5 }?.count shouldBe 3
                    find { it.score == 2 }?.count shouldBe 2
                }
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
