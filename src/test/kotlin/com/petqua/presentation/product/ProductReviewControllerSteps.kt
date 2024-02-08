import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response


fun requestReadAllReviewProducts(
    productId: Long,
    sorter: ProductReviewSorter = REVIEW_DATE_DESC,
    lastViewedId: Long = -1,
    limit: Int = 20,
    score: Int? = null,
    photoOnly: Boolean = false
): Response {
    return Given {
        log().all()
        params(
            "sorter", sorter.name,
            "lastViewedId", lastViewedId,
            "limit", limit,
            "score", score,
            "photoOnly", photoOnly
        )
    } When {
        get("/products/{productId}/reviews", productId)
    } Then {
        log().all()
    } Extract {
        response()
    }
}
