package com.petqua.application.product.review

import com.amazonaws.services.s3.AmazonS3
import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.product.dto.ProductReviewCreateCommand
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType
import com.petqua.test.DataCleaner
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile

@SpringBootTest(webEnvironment = NONE)
class ProductReviewFacadeServiceTest(
    private val productReviewFacadeService: ProductReviewFacadeService,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
    private val dataCleaner: DataCleaner,
    @SpykBean private val amazonS3: AmazonS3,
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

    afterContainer {
        dataCleaner.clean()
    }
})
