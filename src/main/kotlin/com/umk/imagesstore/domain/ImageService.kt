package com.umk.imagesstore.domain

import com.umk.imagesstore.infrastructure.repository.Image
import com.umk.imagesstore.infrastructure.repository.ImageNotFoundException
import com.umk.imagesstore.infrastructure.repository.ImagesRepository
import org.apache.commons.codec.digest.DigestUtils.sha256Hex
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class ImageService(
    private val imagesRepository: ImagesRepository
) {

    fun saveImage(file: MultipartFile): Image {
        assertIsFileSupported(file)
        val image = with (file.bytes) {
            Image(hash = sha256Hex(this), bytes = this.toList())
        }
        imagesRepository.save(image)
        return image
    }

    fun getImage(id: String) = imagesRepository.findByIdOrNull(id) ?: throw ImageNotFoundException(id)

    private fun assertIsFileSupported(file: MultipartFile) {
        with (file.getFileName().getFileExtension()) {
            if (!ALLOWED_FILE_EXTENSION.contains(this)) {
                throw FileTypeIsNotSupportedException(this)
            }
        }
    }

    private fun String.getFileExtension() = File(this).extension

    private fun MultipartFile.getFileName() = this.originalFilename ?: throw FileWithoutNameException()

    companion object {
        private val ALLOWED_FILE_EXTENSION = listOf("jpg", "jpeg")
    }
}