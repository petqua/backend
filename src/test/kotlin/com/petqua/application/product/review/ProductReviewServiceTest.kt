package com.petqua.application.product.review

import com.petqua.application.product.dto.ProductReviewReadQuery
import com.petqua.application.product.dto.UpdateReviewRecommendationCommand
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRecommendation
import com.petqua.domain.product.review.ProductReviewRecommendationRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.product.review.ProductReviewSorter.RECOMMEND_DESC
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType.NOT_FOUND_PRODUCT_REVIEW
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productReview
import com.petqua.test.fixture.productReviewImage
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import java.math.BigDecimal

@SpringBootTest(webEnvironment = NONE)
class ProductReviewServiceTest(
    private val productReviewService: ProductReviewService,
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
    private val productReviewRecommendationRepository: ProductReviewRecommendationRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("상품 후기를 조건에 따라") {
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

        When("전체 별점, 최신순으로 조회 하면") {
            val query = ProductReviewReadQuery(
                productId = product.id,
                loginMemberOrGuest = LoginMemberOrGuest(member.id, member.authority),
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
                loginMemberOrGuest = LoginMemberOrGuest(member.id, member.authority),
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
                loginMemberOrGuest = LoginMemberOrGuest(member.id, member.authority),
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
                loginMemberOrGuest = LoginMemberOrGuest(member.id, member.authority),
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

        When("추천한 상품 후기인 경우") {
            val recommendProductReviewId = savedProductReviews[0].id
            productReviewRecommendationRepository.save(
                ProductReviewRecommendation(
                    productReviewId = recommendProductReviewId,
                    memberId = member.id,
                )
            )

            val query = ProductReviewReadQuery(
                productId = product.id,
                loginMemberOrGuest = LoginMemberOrGuest(member.id, member.authority),
            )
            val response = productReviewService.readAll(query)

            Then("추천 여부를 반환한다") {
                assertSoftly(response.productReviews) {
                    size shouldBe 5
                    find { it.id == recommendProductReviewId }?.recommended shouldBe true
                }
            }
        }

        When("추천 이력이 있어도 요청시 인증 정보를 포함 하지 않으면") {
            val recommendProductReviewId = savedProductReviews[0].id
            productReviewRecommendationRepository.save(
                ProductReviewRecommendation(
                    productReviewId = recommendProductReviewId,
                    memberId = member.id,
                )
            )
            val query = ProductReviewReadQuery(
                productId = product.id,
                loginMemberOrGuest = LoginMemberOrGuest.getGuest(),
            )
            val response = productReviewService.readAll(query)

            Then("추천 여부를 반환 하지 않는다") {
                assertSoftly(response.productReviews) {
                    size shouldBe 5
                    forAll { it.recommended shouldBe false }
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

        When("상품 ID를 입력 하면") {
            val response = productReviewService.readReviewCountStatistics(product.id)

            Then("점수별 개수를 반환한다") {
                assertSoftly(response) {
                    scoreFiveCount shouldBe 3
                    scoreFourCount shouldBe 0
                    scoreThreeCount shouldBe 0
                    scoreTwoCount shouldBe 2
                    scoreOneCount shouldBe 0
                }
            }
        }
    }

    Given("상품 후기 추천을 토글 할 때") {
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

        val savedProductReview = productReviewRepository.save(
            productReview(
                productId = product.id,
                reviewerId = member.id,
                score = 5,
                recommendCount = 1,
                hasPhotos = false,
            )
        )

        When("상품 후기 ID와 회원 ID를 입력 하면") {
            val command = UpdateReviewRecommendationCommand(
                productReviewId = savedProductReview.id,
                memberId = member.id,
            )
            productReviewService.updateReviewRecommendation(command)

            Then("상품 후기를 추천 한다") {
                savedProductReview.recommendCount shouldBe 1
            }
        }

        When("이미 추천한 상품 후기를 다시 추천 하면") {
            productReviewRecommendationRepository.save(
                ProductReviewRecommendation(
                    productReviewId = savedProductReview.id,
                    memberId = member.id,
                )
            )

            val command = UpdateReviewRecommendationCommand(
                productReviewId = savedProductReview.id,
                memberId = member.id,
            )
            productReviewService.updateReviewRecommendation(command)

            Then("상품 후기 추천을 취소 한다") {
                productReviewRecommendationRepository.findByProductReviewIdAndMemberId(
                    savedProductReview.id,
                    member.id,
                )?.shouldBeNull()

                productReviewRepository.findByIdOrThrow(savedProductReview.id).recommendCount shouldBe 0
            }
        }

        When("존재 하지 않는 상품 후기의 ID를 입력하면") {
            val command = UpdateReviewRecommendationCommand(
                productReviewId = Long.MIN_VALUE,
                memberId = member.id,
            )

            Then("상품 후기를 찾을 수 없다는 예외를 반환한다") {
                shouldThrow<ProductReviewException> {
                    productReviewService.updateReviewRecommendation(command)
                }.exceptionType() shouldBe NOT_FOUND_PRODUCT_REVIEW
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
