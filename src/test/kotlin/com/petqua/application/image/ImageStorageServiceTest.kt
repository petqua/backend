package com.petqua.application.image

import com.amazonaws.services.s3.AmazonS3
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import org.mockito.ArgumentMatchers.any
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.net.URL

@SpringBootTest(webEnvironment = NONE)
class ImageStorageServiceTest(
    private val imageStorageService: ImageStorageService,

    @MockkBean
    private val amazonS3: AmazonS3,
) : BehaviorSpec({

    Given("이미지 업로드를 요청할 때") {
        val path = "root/directory/image.jpeg"
        val mockImage = MockMultipartFile(
            "image",
            "image.jpeg",
            MediaType.IMAGE_JPEG_VALUE,
            "image".byteInputStream()
        )

        every { amazonS3.putObject(any(), any(), any(), any()) } returns any()
        every { amazonS3.getUrl(any(), any()) } returns URL("https://storedUrl.com/root/directory/image.jpeg")

        When("파일 경로와 이미지를 입력하면") {
            val imageUrl = imageStorageService.upload(path = path, image = mockImage)

            Then("업로드한다") {
                verify(exactly = 1) {
                    amazonS3.putObject(any(), any(), any(), any())
                }
            }

            Then("이미지 파일을 조회할 수 있는 URL을 반환한다") {
                imageUrl shouldBe "https://domain.com/root/directory/image.jpeg"
            }
        }
    }
})
