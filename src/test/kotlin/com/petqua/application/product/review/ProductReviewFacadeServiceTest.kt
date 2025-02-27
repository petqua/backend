package com.petqua.application.product.review

import com.amazonaws.services.s3.AmazonS3
import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.product.dto.MemberProductReviewReadQuery
import com.petqua.application.product.dto.ProductReviewCreateCommand
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPayment
import com.petqua.domain.order.OrderPaymentRepository
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.OrderStatus.PURCHASE_CONFIRMED
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.option.Sex
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.order
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productReview
import com.petqua.test.fixture.productReviewImage
import com.petqua.test.fixture.store
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.math.BigDecimal

@SpringBootTest(webEnvironment = NONE)
class ProductReviewFacadeServiceTest(
    private val productReviewFacadeService: ProductReviewFacadeService,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
    private val orderRepository: OrderRepository,
    private val memberRepository: MemberRepository,
    private val storeRepository: StoreRepository,
    private val productRepository: ProductRepository,
    private val orderPaymentRepository: OrderPaymentRepository,
    private val dataCleaner: DataCleaner,

    @SpykBean
    private val amazonS3: AmazonS3,
) : BehaviorSpec({

    Given("상품 후기를 작성할 때") {
        val image1 = MockMultipartFile(
            "image1",
            "image1.jpeg",
            MediaType.IMAGE_JPEG_VALUE,
            "image1".byteInputStream()
        )
        val image2 = MockMultipartFile(
            "image2",
            "image2.jpeg",
            MediaType.IMAGE_JPEG_VALUE,
            "image2".byteInputStream()
        )

        When("후기와 이미지를 입력하면") {
            val productReviewId = productReviewFacadeService.create(
                ProductReviewCreateCommand(
                    memberId = 1L,
                    productId = 1L,
                    score = 5,
                    content = "10자가 넘는 정성스러운 후기",
                    images = listOf(image1)
                )
            )

            Then("이미지를 업로드한다") {
                verify(exactly = 1) {
                    amazonS3.putObject(any(), any(), any(), any())
                }
            }

            Then("후기가 저장된다") {
                val productReview = productReviewRepository.findByIdOrThrow(productReviewId)

                productReview.memberId shouldBe 1L
                productReview.productId shouldBe 1L
                productReview.score.value shouldBe 5
                productReview.content.value shouldBe "10자가 넘는 정성스러운 후기"
                productReview.hasPhotos shouldBe true
                productReview.recommendCount shouldBe 0
            }

            Then("후기 이미지가 저장된다") {
                val productReviewImages = productReviewImageRepository.findAll()

                productReviewImages.size shouldBe 1
                productReviewImages[0].productReviewId shouldBe productReviewId
                productReviewImages[0].imageUrl shouldContain "https://domain.com/products/reviews/"
            }
        }

        When("이미지를 여러 개 입력하면") {
            val productReviewId = productReviewFacadeService.create(
                ProductReviewCreateCommand(
                    memberId = 1L,
                    productId = 1L,
                    score = 5,
                    content = "10자가 넘는 정성스러운 후기",
                    images = listOf(image1, image2)
                )
            )

            Then("이미지를 모두 업로드한다") {
                verify(exactly = 2) {
                    amazonS3.putObject(any(), any(), any(), any())
                }
            }

            Then("후기 이미지가 모두 저장된다") {
                val productReviewImages = productReviewImageRepository.findAll()

                productReviewImages.size shouldBe 2
                productReviewImages[0].productReviewId shouldBe productReviewId
                productReviewImages[0].imageUrl shouldContain "https://domain.com/products/reviews/"
                productReviewImages[1].productReviewId shouldBe productReviewId
                productReviewImages[1].imageUrl shouldContain "https://domain.com/products/reviews/"
            }
        }

        When("이미지를 입력하지 않으면") {
            val productReviewId = productReviewFacadeService.create(
                ProductReviewCreateCommand(
                    memberId = 1L,
                    productId = 1L,
                    score = 5,
                    content = "10자가 넘는 정성스러운 후기",
                    images = listOf()
                )
            )

            Then("이미지를 업로드하지 않는다") {
                verify(exactly = 0) {
                    amazonS3.putObject(any(), any(), any(), any())
                }
            }

            Then("후기에 이미지가 없다고 저장된다") {
                val productReview = productReviewRepository.findByIdOrThrow(productReviewId)

                productReview.hasPhotos shouldBe false
            }

            Then("후기 이미지가 저장되지 않는다") {
                val productReviewImages = productReviewImageRepository.findAll()

                productReviewImages.size shouldBe 0
            }
        }

        When("이미지를 10장 초과해 입력하면") {
            val command = ProductReviewCreateCommand(
                memberId = 1L,
                productId = 1L,
                score = 5,
                content = "10자가 넘는 정성스러운 후기",
                images = listOf(
                    image1, image1, image1, image1, image1,
                    image1, image1, image1, image1, image1,
                    image2,
                )
            )

            Then("예외가 발생한다") {
                shouldThrow<ProductReviewException> {
                    productReviewFacadeService.create(command)
                }.exceptionType() shouldBe ProductReviewExceptionType.EXCEEDED_REVIEW_IMAGES_COUNT_LIMIT
            }


            Then("이미지를 업로드하지 않는다") {
                shouldThrow<ProductReviewException> {
                    productReviewFacadeService.create(command)
                }

                verify(exactly = 0) {
                    amazonS3.putObject(any(), any(), any(), any())
                }
            }
        }

        When("별점을 1점 미만, 5점 초과로 입력하면") {
            val command = ProductReviewCreateCommand(
                memberId = 1L,
                productId = 1L,
                score = 6,
                content = "10자가 넘는 정성스러운 후기",
                images = listOf(image1, image2)
            )

            Then("예외가 발생한다") {
                shouldThrow<ProductReviewException> {
                    productReviewFacadeService.create(command)
                }.exceptionType() shouldBe ProductReviewExceptionType.REVIEW_SCORE_OUT_OF_RANGE
            }


            Then("이미지를 업로드하지 않는다") {
                shouldThrow<ProductReviewException> {
                    productReviewFacadeService.create(command)
                }

                verify(exactly = 0) {
                    amazonS3.putObject(any(), any(), any(), any())
                }
            }
        }

        When("후기를 10자 미만, 300자 초과해 입력하면") {
            val command = ProductReviewCreateCommand(
                memberId = 1L,
                productId = 1L,
                score = 5,
                content = "9자로 적은 후기",
                images = listOf(image1, image2)
            )

            Then("예외가 발생한다") {
                shouldThrow<ProductReviewException> {
                    productReviewFacadeService.create(command)
                }.exceptionType() shouldBe ProductReviewExceptionType.REVIEW_CONTENT_LENGTH_OUT_OF_RANGE
            }


            Then("이미지를 업로드하지 않는다") {
                shouldThrow<ProductReviewException> {
                    productReviewFacadeService.create(command)
                }

                verify(exactly = 0) {
                    amazonS3.putObject(any(), any(), any(), any())
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
                totalAmount = BigDecimal.ONE,
                productId = productA.id,
                productName = productA.name,
                thumbnailUrl = productA.thumbnailUrl,
                deliveryMethod = DeliveryMethod.SAFETY,
                sex = Sex.FEMALE,
            )
        )
        val orderB = orderRepository.save(
            order(
                orderNumber = OrderNumber.from("202402211607021ORDERNUMBER"),
                memberId = member.id,
                storeId = store.id,
                storeName = store.name,
                quantity = 1,
                totalAmount = BigDecimal.ONE,
                productId = productB.id,
                productName = productB.name,
                thumbnailUrl = productB.thumbnailUrl,
                deliveryMethod = DeliveryMethod.SAFETY,
                sex = Sex.FEMALE,
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
                hasPhotos = true,
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

        productReviewImageRepository.saveAll(
            listOf(
                productReviewImage(imageUrl = "imageA1", productReviewId = productReviewA.id),
                productReviewImage(imageUrl = "imageA2", productReviewId = productReviewA.id)
            )
        )

        When("회원의 Id를 입력해 조회하면") {
            val memberProductReviewsResponse = productReviewFacadeService.readMemberProductReviews(
                MemberProductReviewReadQuery(
                    memberId = member.id
                )
            )

            Then("리뷰 내역을 반환한다") {
                val memberProductReviews = memberProductReviewsResponse.memberProductReviews

                memberProductReviews.size shouldBe 2

                val memberProductReviewB = memberProductReviews[0]
                memberProductReviewB.reviewId shouldBe productReviewB.id
                memberProductReviewB.memberId shouldBe productReviewB.memberId
                memberProductReviewB.createdAt shouldBe orderB.createdAt
                memberProductReviewB.orderStatus shouldBe PURCHASE_CONFIRMED.name
                memberProductReviewB.storeId shouldBe orderB.orderProduct.storeId
                memberProductReviewB.storeId shouldBe orderB.orderProduct.storeId
                memberProductReviewB.storeName shouldBe orderB.orderProduct.storeName
                memberProductReviewB.productId shouldBe orderB.orderProduct.productId
                memberProductReviewB.productName shouldBe orderB.orderProduct.productName
                memberProductReviewB.productThumbnailUrl shouldBe orderB.orderProduct.thumbnailUrl
                memberProductReviewB.quantity shouldBe orderB.orderProduct.quantity
                memberProductReviewB.sex shouldBe orderB.orderProduct.sex.name
                memberProductReviewB.deliveryMethod shouldBe orderB.orderProduct.deliveryMethod.name
                memberProductReviewB.score shouldBe productReviewB.score.value
                memberProductReviewB.content shouldBe productReviewB.content.value
                memberProductReviewB.recommendCount shouldBe productReviewB.recommendCount
                memberProductReviewB.reviewImages.size shouldBe 0

                val memberProductReviewA = memberProductReviews[1]
                memberProductReviewA.reviewId shouldBe productReviewA.id
                memberProductReviewA.memberId shouldBe productReviewA.memberId
                memberProductReviewA.createdAt shouldBe orderA.createdAt
                memberProductReviewA.orderStatus shouldBe PURCHASE_CONFIRMED.name
                memberProductReviewA.storeId shouldBe orderA.orderProduct.storeId
                memberProductReviewA.storeId shouldBe orderA.orderProduct.storeId
                memberProductReviewA.storeName shouldBe orderA.orderProduct.storeName
                memberProductReviewA.productId shouldBe orderA.orderProduct.productId
                memberProductReviewA.productName shouldBe orderA.orderProduct.productName
                memberProductReviewA.productThumbnailUrl shouldBe orderA.orderProduct.thumbnailUrl
                memberProductReviewA.quantity shouldBe orderA.orderProduct.quantity
                memberProductReviewA.sex shouldBe orderA.orderProduct.sex.name
                memberProductReviewA.deliveryMethod shouldBe orderA.orderProduct.deliveryMethod.name
                memberProductReviewA.score shouldBe productReviewA.score.value
                memberProductReviewA.content shouldBe productReviewA.content.value
                memberProductReviewA.recommendCount shouldBe productReviewA.recommendCount
                memberProductReviewA.reviewImages shouldBe listOf("imageA1", "imageA2")
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
