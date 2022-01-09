package com.umk.imagesstore

import com.umk.imagesstore.domain.ImageService
import com.umk.imagesstore.infrastructure.repository.InMemoryImagesRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import java.io.File

class ImagesStoreApplicationTests {

	private val imagesRepository = InMemoryImagesRepository()
	private val imageService = ImageService(imagesRepository)

	@AfterEach
	fun cleanUp() {
		imagesRepository.deleteAll()
	}

	@Test
	fun `should save image in memory`() {
		// given
		val imageToSave = jpgImage.toMultipartFile()

		// when
		val savedImage = imageService.saveImage(imageToSave)

		// then
		with(imagesRepository.findByIdOrNull(savedImage.id)!!) {
			assertThat(content.bytes).isEqualTo(jpgImage.readBytes().toList())
		}
	}

	@Test
	fun `should get image by id`() {
		// given
		val savedImage = imageService.saveImage(jpgImage.toMultipartFile())

		// when
		val image = imageService.getImage(savedImage.id)

		// then
		with(image) {
			assertThat(id).isEqualTo(savedImage.id)
			assertThat(name).isEqualTo(savedImage.name)
			assertThat(bytes).isEqualTo(savedImage.bytes)
		}
	}

	val jpgImage = File("src/integration/resources/images/image.jpg")

	private fun File.toMultipartFile() = MockMultipartFile(
		this.nameWithoutExtension,
		this.name,
		this.extension,
		this.readBytes()
	)
}
