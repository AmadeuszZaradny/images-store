package com.umk.imagesstore.api

import com.umk.imagesstore.domain.FileTypeIsNotSupportedException
import com.umk.imagesstore.domain.FileWithoutNameException
import com.umk.imagesstore.infrastructure.repository.ImageNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(ImageNotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleImageNotFoundException(request: HttpServletRequest?, e: Exception): String? {
        return e.message
    }

    @ExceptionHandler(FileTypeIsNotSupportedException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleFileTypeIsNotSupportedException(request: HttpServletRequest?, e: Exception): String? {
        return e.message
    }

    @ExceptionHandler(FileWithoutNameException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleFileWithoutNameException(request: HttpServletRequest?, e: Exception): String? {
        return e.message
    }
}