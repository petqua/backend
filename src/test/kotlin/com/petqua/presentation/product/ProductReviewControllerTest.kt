package com.petqua.presentation.product

import com.amazonaws.services.s3.AmazonS3
import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.product.dto.ProductReviewStatisticsResponse
import com.petqua.application.product.dto.ProductReviewsResponse
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.product.review.ProductReviewExceptionType.EXCEEDED_REVIEW_IMAGES_COUNT_LIMIT
import com.petqua.exception.product.review.ProductReviewExceptionType.REVIEW_CONTENT_LENGTH_OUT_OF_RANGE
import com.petqua.exception.product.review.ProductReviewExceptionType.REVIEW_SCORE_OUT_OF_RANGE
import com.petqua.presentation.product.dto.CreateReviewRequest
import com.petqua.presentation.product.dto.UpdateReviewRecommendationRequest
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
import io.kotest.matchers.string.shouldContain
import io.mockk.verify
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.mock.web.MockMultipartFile
import java.math.BigDecimal

class ProductReviewControllerTest(
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,

    @SpykBean
    private val amazonS3: AmazonS3,
) : ApiTestConfig() {

    init {
        Given("상품 후기를 작성할 때") {
            val accessToken = signInAsMember().accessToken
            val product = productRepository.save(product())

            val image1 = MockMultipartFile(
                "image1",
                "image1.jpeg",
                IMAGE_JPEG_VALUE,
                "image1".byteInputStream()
            )
            val image2 = MockMultipartFile(
                "image2",
                "image2.jpeg",
                IMAGE_JPEG_VALUE,
                "image2".byteInputStream()
            )

            When("후기와 이미지를 입력하면") {
                val response = requestCreateProductReview(
                    accessToken = accessToken,
                    productId = product.id,
                    request = CreateReviewRequest(
                        score = 5,
                        content = "this product is good",
                        images = listOf(image1, image2),
                    ),
                )

                Then("201 Created 를 응답한다") {
                    response.statusCode shouldBe CREATED.value()
                }

                Then("이미지를 외부 스토리지에 업로드한다") {
                    verify(exactly = 2) {
                        amazonS3.putObject(any(), any(), any(), any())
                    }
                }

                Then("후기가 저장된다") {
                    val productReview = productReviewRepository.findAll()[0]

                    productReview.memberId shouldBe 1L
                    productReview.productId shouldBe 1L
                    productReview.score.value shouldBe 5
                    productReview.content.value shouldBe "this product is good"
                    productReview.hasPhotos shouldBe true
                    productReview.recommendCount shouldBe 0
                }

                Then("후기 이미지가 저장된다") {
                    val productReview = productReviewRepository.findAll()[0]
                    val productReviewImages = productReviewImageRepository.findAll()

                    productReviewImages.size shouldBe 2
                    productReviewImages[0].productReviewId shouldBe productReview.id
                    productReviewImages[0].imageUrl shouldContain "https://domain.com/products/reviews/"
                    productReviewImages[1].productReviewId shouldBe productReview.id
                    productReviewImages[1].imageUrl shouldContain "https://domain.com/products/reviews/"
                }
            }

            When("이미지없이 후기만 입력하면") {
                val response = requestCreateProductReview(
                    accessToken = accessToken,
                    productId = product.id,
                    request = CreateReviewRequest(
                        score = 5,
                        content = "this product is good",
                        images = listOf(),
                    ),
                )

                Then("201 Created 를 응답한다") {
                    response.statusCode shouldBe CREATED.value()
                }

                Then("이미지를 외부 스토리지에 업로드하지 않는다") {
                    verify(exactly = 0) {
                        amazonS3.putObject(any(), any(), any(), any())
                    }
                }

                Then("후기에 이미지가 없다고 저장된다") {
                    val productReview = productReviewRepository.findAll()[0]

                    productReview.hasPhotos shouldBe false
                }

                Then("후기 이미지가 저장되지 않는다") {
                    val productReviewImages = productReviewImageRepository.findAll()

                    productReviewImages.size shouldBe 0
                }
            }

            When("이미지를 10장 초과해 입력하면") {
                val response = requestCreateProductReview(
                    accessToken = accessToken,
                    productId = product.id,
                    request = CreateReviewRequest(
                        score = 5,
                        content = "this product is good",
                        images = listOf(
                            image1, image1, image1, image1, image1,
                            image1, image1, image1, image1, image1,
                            image2
                        ),
                    ),
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    response.statusCode shouldBe BAD_REQUEST.value()
                    errorResponse.message shouldBe EXCEEDED_REVIEW_IMAGES_COUNT_LIMIT.errorMessage()
                }

                Then("이미지를 외부 스토리지에 업로드하지 않는다") {
                    verify(exactly = 0) {
                        amazonS3.putObject(any(), any(), any(), any())
                    }
                }
            }

            When("별점을 1점 미만, 5점 초과로 입력하면") {
                val response = requestCreateProductReview(
                    accessToken = accessToken,
                    productId = product.id,
                    request = CreateReviewRequest(
                        score = 0,
                        content = "this product is good",
                        images = listOf(image1),
                    ),
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    response.statusCode shouldBe BAD_REQUEST.value()
                    errorResponse.message shouldBe REVIEW_SCORE_OUT_OF_RANGE.errorMessage()
                }

                Then("이미지를 외부 스토리지에 업로드하지 않는다") {
                    verify(exactly = 0) {
                        amazonS3.putObject(any(), any(), any(), any())
                    }
                }
            }

            When("후기를 10자 미만, 300자 초과로 입력하면") {
                val response = requestCreateProductReview(
                    accessToken = accessToken,
                    productId = product.id,
                    request = CreateReviewRequest(
                        score = 5,
                        content = "this product is good".repeat(30),
                        images = listOf(image1),
                    ),
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    response.statusCode shouldBe BAD_REQUEST.value()
                    errorResponse.message shouldBe REVIEW_CONTENT_LENGTH_OUT_OF_RANGE.errorMessage()
                }

                Then("이미지를 외부 스토리지에 업로드하지 않는다") {
                    verify(exactly = 0) {
                        amazonS3.putObject(any(), any(), any(), any())
                    }
                }
            }
        }

        Given("조건에 따라 상품 후기를 조회 하면") {
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

        Given("상품 후기의 통계를 조회 할 때") {
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
                        hasPhotos = false
                    ),
                    productReview(
                        productId = product.id,
                        reviewerId = member.id,
                        score = 5,
                        recommendCount = 2,
                        hasPhotos = true
                    ),
                    productReview(
                        productId = product.id,
                        reviewerId = member.id,
                        score = 5,
                        recommendCount = 3,
                        hasPhotos = false
                    ),
                    productReview(
                        productId = product.id,
                        reviewerId = member.id,
                        score = 2,
                        recommendCount = 4,
                        hasPhotos = true
                    ),
                    productReview(
                        productId = product.id,
                        reviewerId = member.id,
                        score = 2,
                        recommendCount = 5,
                        hasPhotos = true
                    ),
                )
            )

            When("상품 후기의 통계를 조회 하면") {
                val response = requestReadProductReviewCount(productId = product.id)

                Then("해당 상품의 후기 점수별 개수와 만족도, 평균 별점, 총 별점 수를 반환한다") {
                    val responseBody = response.`as`(ProductReviewStatisticsResponse::class.java)

                    assertSoftly(responseBody) {
                        scoreFiveCount shouldBe 3
                        scoreFourCount shouldBe 0
                        scoreThreeCount shouldBe 0
                        scoreTwoCount shouldBe 2
                        scoreOneCount shouldBe 0
                        totalReviewCount shouldBe 5
                        productSatisfaction shouldBe 60
                        averageScore shouldBe 3.8
                    }
                }
            }
        }

        Given("상품 후기를 추천 할 때") {
            val store = storeRepository.save(store(name = "펫쿠아"))
            val memberAccessToken = signInAsMember().accessToken
            val product = productRepository.save(
                product(
                    name = "상품1",
                    storeId = store.id,
                    discountPrice = BigDecimal.ZERO,
                    reviewCount = 0,
                    reviewTotalScore = 0
                )
            )

            val productReview = productReviewRepository.save(
                productReview(
                    productId = product.id,
                    reviewerId = getMemberIdByAccessToken(memberAccessToken),
                    score = 5,
                    recommendCount = 0,
                    hasPhotos = false
                )
            )

            When("상품 후기를 추천 하면") {
                val requestBody = UpdateReviewRecommendationRequest(productReview.id)
                val response = requestUpdateReviewRecommendation(
                    requestBody,
                    memberAccessToken
                )

                Then("해당 상품 후기의 추천 수가 증가한다") {
                    assertSoftly {
                        response.statusCode shouldBe NO_CONTENT.value()
                        productReviewRepository.findByIdOrThrow(productReview.id).recommendCount shouldBe 1
                    }
                }
            }

            When("이미 추천한 상품 후기를 다시 추천 하면") {
                val requestBody = UpdateReviewRecommendationRequest(productReview.id)
                requestUpdateReviewRecommendation(requestBody, memberAccessToken)

                val response = requestUpdateReviewRecommendation(
                    request = requestBody,
                    accessToken = memberAccessToken,
                )

                Then("추천 수가 감소 한다") {
                    assertSoftly {
                        response.statusCode shouldBe NO_CONTENT.value()
                        productReviewRepository.findByIdOrThrow(productReview.id).recommendCount shouldBe 0
                    }
                }
            }
        }
    }
}
