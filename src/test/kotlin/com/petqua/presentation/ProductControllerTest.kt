package com.petqua.presentation

import com.petqua.application.ProductDetailResponse
import com.petqua.domain.ProductRepository
import com.petqua.domain.StoreRepository
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Suppress("NonAsciiCharacters")
class ProductControllerTest(
        @LocalServerPort val port: Int,

        @Autowired val productRepository: ProductRepository,
        @Autowired val storeRepository: StoreRepository,

        ) {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    fun `ID로 상품을 조회한다`() {
        // given
        val store = storeRepository.save(store())
        val productId = productRepository.save(product(storeId = store.id)).id

        // when
        val response = Given {
            log().all()
            pathParam("productId", productId)
        } When {
            get("/product/{productId}")
        } Then {
            log().all()
        } Extract {
            response()
        }

        // then
        val productDetailResponse = response.`as`(ProductDetailResponse::class.java)

        assertSoftly {
            it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
            it.assertThat(productDetailResponse).isEqualTo(ProductDetailResponse(
                    product = product(id = productId, storeId = store.id),
                    storeName = store.name,
                    reviewAverageScore = 0.0
            ))
        }
    }
}
