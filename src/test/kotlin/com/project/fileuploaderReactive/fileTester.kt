package com.project.fileuploaderReactive


import com.project.fileuploaderReactive.Exception.*
import com.project.fileuploaderReactive.Service.FileService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.TestPropertySource
import org.springframework.util.unit.DataSize
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.nio.file.Path

@ExtendWith(MockitoExtension::class)
@TestPropertySource("classpath:resources/application.properties")
class FileUploadControllerTest {
    @InjectMocks
    private lateinit var fservice: FileService

    @Test
    fun uploadsingletest() {
        fservice.maxsize=DataSize.ofMegabytes(5)
        fservice.basePath="D:/Rohan documents/Kotlin projects/fileuploaderReactive/src/main/resources/upload/"
        var filePart:FilePart=mock(FilePart::class.java)

       Mockito.`when`(filePart.filename()).thenReturn("TestImage.jpg")

        Mockito.`when`(filePart.transferTo(Mockito.any(Path::class.java))).thenReturn(Mono.empty())
        val response: Mono<ResponseEntity<String>> = fservice.singleupload(
            filePart.toMono(),
            DataSize.ofKilobytes(5000).toBytes(),
            "8"
        )

   StepVerifier.create(response).expectNextMatches { fp-> fp.statusCode.is2xxSuccessful }
       .verifyComplete()
    }

    @Test
    fun maxfileuploadtest(){
        fservice.maxsize=DataSize.ofMegabytes(5)
        var filePart:FilePart= mock(FilePart::class.java)
        Mockito.`when`(filePart.filename()).thenReturn("TestImage.jpg")
        val response: Mono<ResponseEntity<String>> = fservice.singleupload(
            filePart.toMono(),
            DataSize.ofMegabytes(6).toBytes(),
            "9"
        )
        StepVerifier.create(response).verifyError(MaxSizeUploadException::class.java)

    }
    @Test
    fun extentionuploadtest(){
        var filePart:FilePart= mock(FilePart::class.java)
        Mockito.`when`(filePart.filename()).thenReturn("TestImage.txt")
        val response: Mono<ResponseEntity<String>> = fservice.singleupload(
            filePart.toMono(),
            DataSize.ofKilobytes(5000).toBytes(),
            "9"
        )

        StepVerifier.create(response).verifyError(FileExtentionException::class.java)
    }
    @Test
    fun nulluploadtest(){
        var filePart:FilePart= mock(FilePart::class.java)
        Mockito.`when`(filePart.filename()).thenReturn("")
        val response: Mono<ResponseEntity<String>> = fservice.singleupload(
            filePart.toMono(),
            DataSize.ofKilobytes(5000).toBytes(),
        "8"
        )

        StepVerifier.create(response).verifyError(NullException::class.java)
    }

    @Test
    fun duplicateuploadtest(){
        fservice.maxsize=DataSize.ofMegabytes(5)
        fservice.basePath="D:/Rohan documents/Kotlin projects/fileuploaderReactive/src/main/resources/upload/"
        var filePart1:FilePart= mock(FilePart::class.java)
        Mockito.`when`(filePart1.filename()).thenReturn("Neymar.jpeg")
        val response: Mono<ResponseEntity<String>> = fservice.singleupload(
            filePart1.toMono(),
            DataSize.ofKilobytes(5000).toBytes(),
            "8"
        )
        StepVerifier.create(response).verifyError(DuplicatefileException::class.java)
    }




    @Test
    fun uploadmultipletest(){
        fservice.maxsize=DataSize.ofMegabytes(5)
        fservice.basePath="D:/Rohan documents/Kotlin projects/fileuploaderReactive/src/main/resources/upload/"
        var filePart1:FilePart= mock(FilePart::class.java)
        var filePart2:FilePart= mock(FilePart::class.java)


        Mockito.`when`(filePart1.filename()).thenReturn("Test1.jpeg")
        Mockito.`when`(filePart2.filename()).thenReturn("Test2.jpeg")
        Mockito.`when`(filePart1.transferTo(Mockito.any(Path::class.java))).thenReturn(Mono.empty())
        Mockito.`when`(filePart2.transferTo(Mockito.any(Path::class.java))).thenReturn(Mono.empty())
        val response = fservice.multiupload(
            Flux.just(filePart1,filePart2),
            DataSize.ofKilobytes(5000).toBytes(),"8"
        )

        StepVerifier.create(response).expectNextMatches { path->path.endsWith(filePart1.filename()) }
            .expectNextMatches { path->path.endsWith(filePart2.filename()) }
            .verifyComplete()

    }
//    @Test
//    fun fileextentionmultipletest(){
//        var filePart1:FilePart= mock(FilePart::class.java)
//        var filePart2:FilePart= mock(FilePart::class.java)
//
//
//        Mockito.`when`(filePart1.filename()).thenReturn("kjrwfsbvfhbv.jpg")
//        Mockito.`when`(filePart2.filename()).thenReturn("Test2.txt")
//        Mockito.`when`(filePart1.transferTo(Mockito.any(Path::class.java))).thenReturn(Mono.empty())
//        Mockito.`when`(filePart2.transferTo(Mockito.any(Path::class.java))).thenReturn(Mono.empty())

//        val response2 = fservice.multiupload( Flux.just(filePart1,filePart2),DataSize.ofKilobytes(5000).toBytes(),"rohan")
//
//
//        StepVerifier.create(response2).verifyError(FileExtentionException::class.java)
//
//    }
//    @Test
//    fun sizemultipletest(){
//        var filePart1:FilePart= mock(FilePart::class.java)
//        var filePart2:FilePart= mock(FilePart::class.java)
//
//        Mockito.`when`(filePart1.filename()).thenReturn("Test1.jpg")
//        Mockito.`when`(filePart2.filename()).thenReturn("Test2.txt")
//
//        val response = fservice.multiupload( Flux.just(filePart1),DataSize.ofMegabytes(6).toBytes())
//        val response2 = fservice.multiupload( Flux.just(filePart2),DataSize.ofMegabytes(6).toBytes())
//        val response3= Flux.concat(response,response2)
//        StepVerifier.create(response3).verifyError(MaxSizeUploadException::class.java)
//
//    }

    @Test
    fun downloadtest(){
        fservice.basePath="D:/Rohan documents/Kotlin projects/fileuploaderReactive/src/main/resources/upload/"
     val response:Mono<ResponseEntity<Resource>> = fservice.downloadfunc("Neymar.jpeg", "9")
        StepVerifier.create(response).expectNextMatches { fp-> fp.statusCode.is2xxSuccessful }
            .verifyComplete()


    }
    @Test
    fun filenotfoundtest(){
        fservice.basePath="D:/Rohan documents/Kotlin projects/fileuploaderReactive/src/main/resources/upload/"
        val response:Mono<ResponseEntity<Resource>> = fservice.downloadfunc("10th Birthday-846.jpg", "9")
        StepVerifier.create(response).verifyError(FileNotfound::class.java)



    }
    }






