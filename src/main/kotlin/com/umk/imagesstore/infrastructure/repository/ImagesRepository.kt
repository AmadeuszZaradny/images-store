package com.umk.imagesstore.infrastructure.repository

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.collections.HashMap

interface ImagesRepository {
    fun save(image: DbImageContent): DbImage
    fun findByIdOrNull(id: String): DbImage?
}

class InMemoryImagesRepository: ImagesRepository {

    private val storage: MutableMap<String, DbImage> = HashMap()

    override fun save(image: DbImageContent): DbImage =
        DbImage(UUID.randomUUID().toString(), image)
            .also { storage[it.id] = DbImage(it.id, image) }

    override fun findByIdOrNull(id: String) = storage[id]
}

class MongoImagesRepository(
    private val gridFsTemplate: GridFsTemplate
): ImagesRepository {

    override fun save(image: DbImageContent): DbImage {
        val input = ByteArrayInputStream(image.bytes.toByteArray())
        val id = gridFsTemplate.store(input, IMAGE_FILE_NAME)
        return DbImage(id.toString(), image)
    }

    override fun findByIdOrNull(id: String): DbImage? {
        val file = gridFsTemplate.findOne(query(Criteria(ID_FILED).`is`(id)))
        val content = gridFsTemplate.getResource(file).content
        return DbImage(id, DbImageContent(content.readAllBytes().toList()))
    }

    companion object {
        private const val IMAGE_FILE_NAME = "image"
        private const val ID_FILED = "_id"
    }
}


data class DbImage(val id: String, val content: DbImageContent)

data class DbImageContent(val bytes: List<Byte>)