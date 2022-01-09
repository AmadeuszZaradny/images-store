package com.umk.imagesstore

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap

@SpringBootTest(
    classes = [ImagesStoreApplication::class],
    properties = ["application.environment=integration"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
class BaseIntegration(
    val restTemplate: TestRestTemplate
) {

    @LocalServerPort
    protected var port: Int = 0

    fun localUrl(endpoint: String): String = "http://localhost:$port$endpoint"

    final inline fun <reified T: Any> post(
        endpoint: String,
        body: Any,
        contentType: String = MULTIPART_FORM_DATA_VALUE
    ): ResponseEntity<T> =
        restTemplate.exchange(
            url = localUrl(endpoint),
            method = HttpMethod.POST,
            requestEntity = HttpEntity(body, HttpHeaders().apply { add(CONTENT_TYPE, contentType) }))

    final inline fun <reified T: Any> get(
        endpoint: String,
        contentType: String = MediaType.IMAGE_JPEG_VALUE
    ): ResponseEntity<T> =
        restTemplate.exchange(
            url = localUrl(endpoint),
            method = HttpMethod.GET,
            requestEntity = HttpEntity(null, HttpHeaders().apply { add(CONTENT_TYPE, contentType) })
        )

    fun getMultipartFileRequestBody(filepath: String): LinkedMultiValueMap<String, Any> {
        val parameters = LinkedMultiValueMap<String, Any>()
        parameters.add("file", ClassPathResource(filepath))
        return parameters
    }
}