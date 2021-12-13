package com.umk.imagesstore.infrastructure.repository

import java.lang.RuntimeException

class ImageNotFoundException(hash: String): RuntimeException("Image with hash $hash is not found.")