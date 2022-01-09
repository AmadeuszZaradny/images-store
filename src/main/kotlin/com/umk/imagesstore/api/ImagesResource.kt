package com.umk.imagesstore.api

import com.umk.imagesstore.domain.ImageService
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/images")
class ImagesResource(
    private val imageService: ImageService
) {

    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    fun saveImage(@RequestParam("file") file: MultipartFile): SaveImageResponse {
        return with(imageService.saveImage(file)) {
            SaveImageResponse(
                id = this.id,
                name = this.name,
            )
        }
    }

    @GetMapping("/{id}", produces = [IMAGE_JPEG_VALUE])
    fun getImage(@PathVariable("id") id: String): ByteArray = imageService.getImage(id).bytes.toByteArray()
}

data class SaveImageResponse(val id: String, val name: String)