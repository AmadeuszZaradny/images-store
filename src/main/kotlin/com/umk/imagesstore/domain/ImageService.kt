package com.umk.imagesstore.domain

import com.umk.imagesstore.infrastructure.repository.DbImage
import com.umk.imagesstore.infrastructure.repository.DbImageContent
import com.umk.imagesstore.infrastructure.repository.ImageNotFoundException
import com.umk.imagesstore.infrastructure.repository.ImagesRepository
import com.umk.imagesstore.infrastructure.repository.SaveImageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class ImageService(
    private val imagesRepository: ImagesRepository
) {

    fun saveImage(file: MultipartFile): Image {
        assertIsFileSupported(file)
        return imagesRepository.save(file.toSaveImageRequest()).toDomain()
    }

    fun getImage(id: String): Image =
        imagesRepository.findByIdOrNull(id)?.toDomain() ?: throw ImageNotFoundException(id)

    private fun assertIsFileSupported(file: MultipartFile) {
        with (file.getFileName().getFileExtension()) {
            if (!ALLOWED_FILE_EXTENSION.contains(this)) {
                throw FileTypeIsNotSupportedException(this)
            }
        }
    }

    private fun String.getFileExtension() = File(this).extension

    private fun MultipartFile.getFileName() = this.originalFilename ?: throw FileWithoutNameException()

    private fun DbImage.toDomain() = Image(id = this.id, name = this.fileName, bytes = this.content.bytes)

    private fun MultipartFile.toSaveImageRequest() = SaveImageRequest(
        fileName = this.getFileName(),
        content = DbImageContent(this.bytes.toList())
    )

    companion object {
        private val ALLOWED_FILE_EXTENSION = listOf("jpg", "jpeg")
    }
}