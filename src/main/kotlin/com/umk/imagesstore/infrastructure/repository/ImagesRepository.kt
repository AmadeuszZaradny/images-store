package com.umk.imagesstore.infrastructure.repository

import org.springframework.stereotype.Repository

interface ImagesRepository {
    fun save(image: Image)
    fun findByIdOrNull(id: String): Image?
}

@Repository
private class InMemoryImagesRepository: ImagesRepository {

    private val storage: MutableMap<String, Image> = HashMap()

    override fun save(image: Image) {
        storage[image.hash] = image
    }

    override fun findByIdOrNull(id: String) = storage[id]
}

data class Image(val hash: String, val bytes: List<Byte>)