package com.umk.imagesstore.domain

import java.lang.RuntimeException

class FileTypeIsNotSupportedException(extension: String): RuntimeException("File extension $extension is not supported.")

class FileWithoutNameException: RuntimeException("File must have a name.")