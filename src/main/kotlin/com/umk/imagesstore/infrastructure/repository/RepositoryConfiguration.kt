package com.umk.imagesstore.infrastructure.repository

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.gridfs.GridFsTemplate

@Configuration
class RepositoryConfiguration {

    @Bean
    @Primary
    @ConditionalOnProperty(value = [ "mongo.enabled" ], havingValue = "false", matchIfMissing = true)
    fun inMemoryImagesRepository(): ImagesRepository {
        return InMemoryImagesRepository()
    }

    @Bean
    @ConditionalOnProperty(value = [ "mongo.enabled" ], havingValue = "true")
    fun mongoImagesRepository(gridFsTemplate: GridFsTemplate): ImagesRepository {
        return MongoImagesRepository(gridFsTemplate)
    }
}