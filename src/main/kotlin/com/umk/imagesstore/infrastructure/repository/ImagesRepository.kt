package com.umk.imagesstore.infrastructure.repository

import com.mongodb.client.gridfs.model.GridFSFile
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.collections.HashMap

interface ImagesRepository {
    fun save(request: SaveImageRequest): DbImage
    fun findByIdOrNull(id: String): DbImage?
}

class InMemoryImagesRepository: ImagesRepository {

    private val storage: MutableMap<String, DbImage> = HashMap()

    override fun save(request: SaveImageRequest): DbImage =
        DbImage(UUID.randomUUID().toString(), request.fileName, request.content)
            .also { storage[it.id] = DbImage(it.id, request.fileName, request.content) }

    override fun findByIdOrNull(id: String) = storage[id]

    fun deleteAll() {
        storage.clear()
    }

    fun findAll() = storage.values.toList()
}

class MongoImagesRepository(
    private val gridFsTemplate: GridFsTemplate
): ImagesRepository {

    override fun save(request: SaveImageRequest): DbImage {
        val input = ByteArrayInputStream(request.content.bytes.toByteArray())
        val id = gridFsTemplate.store(input, request.fileName, IMAGE_FILE_NAME)
        return DbImage(id.toString(), request.fileName, request.content)
    }

    override fun findByIdOrNull(id: String): DbImage? {
        val file: GridFSFile? = gridFsTemplate.find(query(Criteria(ID_FILED).`is`(id))).firstOrNull()
        return if (file !== null) {
            val content = gridFsTemplate.getResource(file).content
            DbImage(id, file.filename, DbImageContent(content.readAllBytes().toList()))
        } else {
            null
        }
    }

    companion object {
        private const val IMAGE_FILE_NAME = "image"
        private const val ID_FILED = "_id"
    }
}

data class SaveImageRequest(
    val fileName: String,
    val content: DbImageContent
)

data class DbImage(val id: String, val fileName: String, val content: DbImageContent) {
    fun getContentBytes(): ByteArray = content.bytes.toByteArray()
}

data class DbImageContent(val bytes: List<Byte>)