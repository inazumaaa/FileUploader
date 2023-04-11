package com.project.fileuploaderReactive.Exception

import com.project.fileuploaderReactive.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import reactor.core.publisher.Mono

@ControllerAdvice
class Exception {

    @ExceptionHandler(FileExtentionException::class)
    fun handleargumentexception(): ResponseEntity<Response> {
        val response: Response = Response("File type is not supported.", 400)
        return ResponseEntity.badRequest().body(response)
    }
    @ExceptionHandler(MaxSizeUploadException::class)
    fun handlemaxsize(): ResponseEntity<Response> {
        val response: Response = Response("Max Size for uploading is 5MB.", 400)
        return ResponseEntity.badRequest().body(response)
    }
    @ExceptionHandler(DuplicatefileException::class)
    fun handleduplicate(): Mono<ResponseEntity<Response>> {
        val response  = Response("File already exists in the directory.", 400)
        return Mono.just(ResponseEntity.badRequest().body(response))
    }
    @ExceptionHandler(NullException::class)
    fun nullexception(): ResponseEntity<Response> {
        val response: Response = Response("Null file found.", 400)
        return ResponseEntity.badRequest().body(response)
    }
    @ExceptionHandler(FileNotfound::class)
    fun filenotfound(): ResponseEntity<Response> {
        val response: Response = Response("File Not Found in the directory.", 400)
        return ResponseEntity.badRequest().body(response)
    }
    @ExceptionHandler(NullFileEntered::class)
    fun nofileentered(): ResponseEntity<Response> {
        val response: Response = Response("No file entered for accessing. ", 400)
        return ResponseEntity.badRequest().body(response)
    }
    @ExceptionHandler(SpaceException::class)
    fun spaceexception(): ResponseEntity<Response> {
        val response: Response = Response("File name contains spaces. Please Enter a file without spaces. ", 400)
        return ResponseEntity.badRequest().body(response)
    }
}