package com.swm_standard.phote.external.aws

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.swm_standard.phote.common.exception.BadRequestException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Component
class S3Service(
    @Value("\${cloud.aws.s3.bucketName}")
    private val bucket: String,
    private val amazonS3: AmazonS3,
) {
    companion object {
        const val TYPE_IMAGE = "image"
    }

    fun uploadImage(multipartFile: MultipartFile): String {
        val originalFilename =
            multipartFile.originalFilename
                ?: throw BadRequestException("image 미입력")
        FileValidate.checkImageFormat(originalFilename)
        val fileName = "${UUID.randomUUID()}-$originalFilename"
        val objectMetadata =
            setFileDateOption(
                type = TYPE_IMAGE,
                file = getFileExtension(originalFilename),
                multipartFile = multipartFile,
            )
        amazonS3.putObject(bucket, fileName, multipartFile.inputStream, objectMetadata)
        val imageUrl = amazonS3.getUrl(bucket, fileName).toString()
        return imageUrl
    }

    fun uploadChatGptImage(multipartFile: MultipartFile): String {
        val originalFilename =
            multipartFile.originalFilename
                ?: throw BadRequestException("image 미입력")
        FileValidate.checkImageFormat(originalFilename)
        val fileName = "${UUID.randomUUID()}-$originalFilename"
        val objectMetadata =
            setFileDateOption(
                type = TYPE_IMAGE,
                file = getFileExtension(originalFilename),
                multipartFile = multipartFile,
            )

        // uploadImage() 와 다른 부분
        amazonS3.putObject("$bucket/chat-gpt", fileName, multipartFile.inputStream, objectMetadata)
        val imageUrl = amazonS3.getUrl("$bucket/chat-gpt", fileName).toString()

        return imageUrl
    }

    private fun getFileExtension(fileName: String): String {
        val extensionIndex = fileName.lastIndexOf('.')
        return fileName.substring(extensionIndex + 1)
    }

    private fun setFileDateOption(
        type: String,
        file: String,
        multipartFile: MultipartFile,
    ): ObjectMetadata {
        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = "/$type/${getFileExtension(file)}"
        objectMetadata.contentLength = multipartFile.inputStream.available().toLong()
        return objectMetadata
    }
}

class FileValidate {
    companion object {
        private val IMAGE_EXTENSIONS: List<String> = listOf("jpg", "png", "gif", "webp")

        fun checkImageFormat(fileName: String) {
            val extensionIndex = fileName.lastIndexOf('.')
            if (extensionIndex == -1) {
                throw BadRequestException("파일 확장자가 없음")
            }
            val extension = fileName.substring(extensionIndex + 1)
            require(IMAGE_EXTENSIONS.contains(extension)) {
                throw BadRequestException("지원하지 않는 파일 확장자")
            }
        }
    }
}
