package com.project.fileuploaderReactive.Controller

import com.project.fileuploaderReactive.Exception.*
import com.project.fileuploaderReactive.Service.FileService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.IOException

@RestController
class FileController {

    @Autowired
    lateinit var fservice:FileService

    @PostMapping("/file-single")
    @Throws(IOException::class)
    fun uploadsingle(

        @RequestPart("file") filePartMono: Mono<FilePart>, @RequestHeader("Content-Length")size:Long,@RequestPart("user") userid:String): Mono<ResponseEntity<String>> {
        return fservice.singleupload(filePartMono,size,userid)

    }

    @PostMapping("/file-multi", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Throws(IOException::class)
    fun uploadmulti( @RequestPart("files") partFlux: Flux<FilePart>, @RequestHeader("Content-Length")size:Long,@RequestPart("user") userid:String): Flux<String> {

        return fservice.multiupload(partFlux,size,userid)

    }


    @GetMapping(value = ["/download/{userid}/{fileName}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @Throws(IOException::class)
    fun download(@PathVariable("fileName") fileName: String,@PathVariable("userid") userid: String): Mono<ResponseEntity<Resource>> {

    return fservice.downloadfunc(fileName,userid)


    }


}