package com.petqua.domain.product.review

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.domain.delivery.DeliveryMethod.SAFETY
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPayment
import com.petqua.domain.order.OrderPaymentRepository
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.OrderStatus.PURCHASE_CONFIRMED
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.dto.ProductReviewReadCondition
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.domain.product.review.ProductReviewSorter.RECOMMEND_DESC
import com.petqua.domain.store.StoreRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.order
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productReview
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.math.BigDecimal.ONE

@SpringBootTest
class ProductReviewCustomRepositoryImplTest(
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val orderRepository: OrderRepository,
    private val orderPaymentRepository: OrderPaymentRepository,
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

    Given("사용자가 리뷰를 작성한 후 자신의 리뷰 내역을 조회할 때") {
        val member = memberRepository.save(member(nickname = "쿠아"))
        val store = storeRepository.save(store(name = "펫쿠아"))
        val productA = productRepository.save(
            product(
                name = "상품A",
                storeId = store.id,
                discountPrice = BigDecimal.ZERO,
                reviewCount = 0,
                reviewTotalScore = 0
            )
        )
        val productB = productRepository.save(
            product(
                name = "상품B",
                storeId = store.id,
                discountPrice = BigDecimal.ZERO,
                reviewCount = 0,
                reviewTotalScore = 0
            )
        )
        val orderA = orderRepository.save(
            order(
                orderNumber = OrderNumber.from("202402211607020ORDERNUMBER"),
                memberId = member.id,
                storeId = store.id,
                storeName = store.name,
                quantity = 1,
                totalAmount = ONE,
                productId = productA.id,
                productName = productA.name,
                thumbnailUrl = productA.thumbnailUrl,
                deliveryMethod = SAFETY,
                sex = FEMALE,
            )
        )
        val orderB = orderRepository.save(
            order(
                orderNumber = OrderNumber.from("202402211607021ORDERNUMBER"),
                memberId = member.id,
                storeId = store.id,
                storeName = store.name,
                quantity = 1,
                totalAmount = ONE,
                productId = productB.id,
                productName = productB.name,
                thumbnailUrl = productB.thumbnailUrl,
                deliveryMethod = SAFETY,
                sex = FEMALE,
            )
        )
        orderPaymentRepository.saveAll(
            listOf(
                OrderPayment(
                    orderId = orderA.id,
                    status = PURCHASE_CONFIRMED
                ),
                OrderPayment(
                    orderId = orderB.id,
                    status = PURCHASE_CONFIRMED
                )
            )
        )

        val productReviewA = productReviewRepository.save(
            productReview(
                productId = productA.id,
                reviewerId = member.id,
                score = 5,
                recommendCount = 1,
                hasPhotos = false,
                content = "상품A 정말 좋아요!"
            )
        )
        val productReviewB = productReviewRepository.save(
            productReview(
                productId = productB.id,
                reviewerId = member.id,
                score = 5,
                recommendCount = 1,
                hasPhotos = false,
                content = "상품B 정말 좋아요!"
            )
        )

        When("자신의 리뷰 내역을 조회하면") {
            val reviews = productReviewRepository.findMemberProductReviewBy(member.id, CursorBasedPaging())

            Then("리뷰가 최신순으로 반환된다") {
                reviews.size shouldBe 2

                val memberProductReviewB = reviews[0]
                memberProductReviewB.reviewId shouldBe productReviewB.id
                memberProductReviewB.memberId shouldBe productReviewB.memberId
                memberProductReviewB.createdAt shouldBe orderB.createdAt
                memberProductReviewB.orderStatus shouldBe PURCHASE_CONFIRMED
                memberProductReviewB.storeId shouldBe orderB.orderProduct.storeId
                memberProductReviewB.storeId shouldBe orderB.orderProduct.storeId
                memberProductReviewB.storeName shouldBe orderB.orderProduct.storeName
                memberProductReviewB.productId shouldBe orderB.orderProduct.productId
                memberProductReviewB.productName shouldBe orderB.orderProduct.productName
                memberProductReviewB.productThumbnailUrl shouldBe orderB.orderProduct.thumbnailUrl
                memberProductReviewB.quantity shouldBe orderB.orderProduct.quantity
                memberProductReviewB.sex shouldBe orderB.orderProduct.sex
                memberProductReviewB.deliveryMethod shouldBe orderB.orderProduct.deliveryMethod
                memberProductReviewB.score shouldBe productReviewB.score
                memberProductReviewB.content.value shouldBe productReviewB.content.value
                memberProductReviewB.recommendCount shouldBe productReviewB.recommendCount

                val memberProductReviewA = reviews[1]
                memberProductReviewA.reviewId shouldBe productReviewA.id
                memberProductReviewA.memberId shouldBe productReviewA.memberId
                memberProductReviewA.createdAt shouldBe orderA.createdAt
                memberProductReviewA.orderStatus shouldBe PURCHASE_CONFIRMED
                memberProductReviewA.storeId shouldBe orderA.orderProduct.storeId
                memberProductReviewA.storeId shouldBe orderA.orderProduct.storeId
                memberProductReviewA.storeName shouldBe orderA.orderProduct.storeName
                memberProductReviewA.productId shouldBe orderA.orderProduct.productId
                memberProductReviewA.productName shouldBe orderA.orderProduct.productName
                memberProductReviewA.productThumbnailUrl shouldBe orderA.orderProduct.thumbnailUrl
                memberProductReviewA.quantity shouldBe orderA.orderProduct.quantity
                memberProductReviewA.sex shouldBe orderA.orderProduct.sex
                memberProductReviewA.deliveryMethod shouldBe orderA.orderProduct.deliveryMethod
                memberProductReviewA.score shouldBe productReviewA.score
                memberProductReviewA.content.value shouldBe productReviewA.content.value
                memberProductReviewA.recommendCount shouldBe productReviewA.recommendCount
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
