package com.umk.imagesstore

import com.umk.imagesstore.api.SaveImageResponse
import com.umk.imagesstore.infrastructure.repository.DbImageContent
import com.umk.imagesstore.infrastructure.repository.MongoImagesRepository
import com.umk.imagesstore.infrastructure.repository.SaveImageRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpStatus
import java.io.File


class ImageResourceSpec(
    @Autowired val testRestTemplate: TestRestTemplate,
    @Autowired val mongoTemplate: MongoTemplate,
    @Autowired val imagesRepository: MongoImagesRepository
): BaseIntegration(testRestTemplate) {

    val jpgImage = File("src/integration/resources/images/image.jpg")
    val jpegImage = File("src/integration/resources/images/image.jpeg")

    @AfterEach
    fun cleanUpMongo() {
        mongoTemplate.collectionNames.forEach { mongoTemplate.dropCollection(it) }
    }

    @Test
    fun `should save image`() {
        // given
        val request = getMultipartFileRequestBody("images/image.jpg")

        // when
        val response = post<SaveImageResponse>("/images", request)

        // then
        with(response) {
            assertThat(statusCode).isEqualTo(HttpStatus.OK)
            assertThat(body!!.name).isEqualTo("image.jpg")
        }

        // and
        assertThat(imagesRepository.findByIdOrNull(response.body!!.id)!!.getContentBytes()).isEqualTo(jpgImage.readBytes())
    }

    @Test
    fun `should not save image when extension is not allowed`() {
        // given
        val request = getMultipartFileRequestBody("images/image.png")

        // when
        val response = post<Void>("/images", request)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        // and
        assertGridFsIsEmpty()
    }

    @Test
    fun `should get image by id`() {
        // given
        val existingImage = imagesRepository.save(SaveImageRequest("new-image.jpeg", jpegImage.toDbImageContent()))

        // when
        val response = get<ByteArray>("/images/${existingImage.id}")

        // then
        with(response) {
            assertThat(statusCode).isEqualTo(HttpStatus.OK)
            assertThat(body).isEqualTo(existingImage.content.bytes.toByteArray())
        }
    }

    @Test
    fun `should respond with NOT_FOUND status code when image does not exist`() {
        // when
        val response = get<ByteArray>("/images/not-existing-image")

        // then
        with(response) {
            assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }
    }

    fun assertGridFsIsEmpty() {
        listOf("fs.files", "fs.chunks").forEach {
            assertThat(mongoTemplate.getCollection(it).countDocuments()).isEqualTo(0)
        }
    }

    fun File.toDbImageContent() = DbImageContent(readBytes().toList())
}