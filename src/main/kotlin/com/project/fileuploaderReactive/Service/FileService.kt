package com.project.fileuploaderReactive.Service

import com.google.common.hash.Hashing
import com.project.fileuploaderReactive.Exception.*
import com.project.fileuploaderReactive.FileType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.util.unit.DataSize
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

@Service
class FileService {

//    @Autowired
//    lateinit var fileRepository: FileRepository


    var logger:Logger=LoggerFactory.getLogger(FileService::class.java)

    @Value("\${file.upload.path}")
     lateinit var basePath :String

    val stringset= setOf("jpeg","png","pdf","csv","xls","jpg","doc")
    @Value("\${max.size}")
    lateinit var maxsize:DataSize

    fun getfilepath(userid: String): Array<out String>? {
        return File(basePath.plus("/").plus(userid)).list()
    }

    fun isValidFileType(extension: String): Boolean {
        return try {
            FileType.valueOf(extension.toUpperCase())
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun singleupload(filePartMono: Mono<FilePart>, size: Long, userid: String): Mono<ResponseEntity<String>> {

        var user=userid
        val regex=Regex("\\s")
            return filePartMono
                .doOnNext {
                         if (it.filename() =="" )
                    throw NullException()
                    if(!isValidFileType(it.filename().split(".").last()))
                        throw FileExtentionException()
                    if (size>maxsize.toBytes())
                        throw MaxSizeUploadException()
                    if(getfilepath(userid)?.contains(it.filename()) == true)
                        throw DuplicatefileException()
                    if(regex.containsMatchIn(it.filename()))
                        throw SpaceException()

                }
                .flatMap   {fp->
//                        fp -> if (fp.filename() =="" )
//                    throw NullException()
//                    if(!isValidFileType(fp.filename().split(".").last()))
//                        throw FileExtentionException()
//                    if (size>maxsize)
//                        throw MaxSizeUploadException()
//                    if(getfilepath()?.contains(fp.filename()) == true)
//                        throw DuplicatefileException()
//                    if(regex.containsMatchIn(fp.filename()))
//                        throw SpaceException()

//                    else
//                        logger.info("Received File:{}",fp.filename())
//                    val target:Path=Paths.get(basePath.plus("/").plus(fp.filename()))
//                    val downloadlink="http://localhost:8080".plus("/download/").plus(fp.filename())
//
//                    fp.transferTo(target).thenReturn(ResponseEntity.ok().body(downloadlink))
                    if (user.isEmpty()){
                        logger.info("Received File:{}",fp.filename())
                        user= ifidisnull()
                        createFolder(basePath,user)
                        var target1:Path=Paths.get(basePath.plus("/").plus(user).plus("/").plus(fp.filename()))
                        val downloadlink1 = "http://localhost:8080".plus("/download/").plus(fp.filename().plus(user))
                        fp.transferTo(target1).thenReturn(ResponseEntity.ok().body(downloadlink1))
                    }
                    else {
                        logger.info("Received File:{}", fp.filename())
                        createFolder(basePath, userid)
                        var target2: Path = Paths.get(basePath.plus("/").plus(userid).plus("/").plus(fp.filename()))
                        val downloadlink2 = "http://localhost:8080".plus("/download/").plus(fp.filename().plus("/").plus(userid))
                        fp.transferTo(target2).thenReturn(ResponseEntity.ok().body(downloadlink2))
                    }

                }
        }

    private fun ifidisnull(): String {
       return Hashing.sha256().hashString(LocalDateTime.now().toString(), Charsets.UTF_8).toString()

    }


    fun createFolder(directoryPath: String,id:String):String{
        var filePath="$directoryPath/$id"
        val folder=File(filePath)
        if (!folder.exists()) {
            folder.mkdir()
        }
        return filePath
    }

    fun multiupload(partFlux: Flux<FilePart>, size: Long, userid: String): Flux<String> {

        var user=userid
        val regex=Regex("\\s")
        return partFlux
            .doOnNext { if (it.filename() == "")
                throw NullException()
                if (!isValidFileType(it.filename().split(".").last()))
                    throw FileExtentionException()
                if (size > maxsize.toBytes())
                    throw MaxSizeUploadException()
                if (getfilepath(userid)?.contains(it.filename()) == true)
                    throw DuplicatefileException()
                if(regex.containsMatchIn(it.filename()))
                    throw SpaceException()

            }
            .flatMap { fp ->
                if (user.isEmpty()) {
                    logger.info("Received File:{}", fp.filename())
                    user = ifidisnull()
                    createFolder(basePath, user)
                    var target1: Path = Paths.get(basePath.plus("/").plus(user).plus("/").plus(fp.filename()))
                    val downloadlink1 = "http://localhost:8080".plus("/download/").plus(user).plus("/").plus(fp.filename())
                    fp.transferTo(target1).thenReturn(downloadlink1)


                }
                else
                {
                    logger.info("Received File:{}", fp.filename())
                    createFolder(basePath, userid)
                    var target2: Path = Paths.get(basePath.plus("/").plus(userid).plus("/").plus(fp.filename()))
                    val downloadlink2 = "http://localhost:8080".plus("/download/").plus(userid).plus("/").plus(fp.filename())
                    fp.transferTo(target2).thenReturn(downloadlink2)
                }
//                try {
//                    logger.info("Received File:{}", fp.filename())
//                    val target:Path=Paths.get(basePath.plus("/").plus(fp.filename()))
//                    val downloadlink="http://localhost:8080".plus("/download/").plus(fp.filename())
//                    fp.transferTo(target).thenReturn(downloadlink)
//                    }
//                catch (e: Exception) {
//
//                        logger.error("File transfer failed.")
//                        throw DuplicatefileException()
//                    }
            }
    }

    fun downloadfunc(fileName: String, userid: String): Mono<ResponseEntity<Resource>> {

        return Mono.fromSupplier {
            val fileLocation = "$basePath/$userid/$fileName"
            println(fileLocation)
            val path = Paths.get(fileLocation).toAbsolutePath().toString()
            println(path)
            FileSystemResource(path)
        }
            .flatMap {  fp ->
                if(getfilepath(userid)?.contains(fileName) != true) {
                    println(getfilepath(userid))
                    throw FileNotfound()
                }
                val headers = HttpHeaders()
                headers.setContentDispositionFormData(fileName, fileName)
                Mono.just(
                    ResponseEntity.ok()
                        .headers(headers)
                        .body(fp)
                )
            }
    }





    }


