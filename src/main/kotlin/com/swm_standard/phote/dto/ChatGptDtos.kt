package com.swm_standard.phote.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.swm_standard.phote.external.chatgpt.PROMPT

data class RequestMessage(
    val role: String,
    val content: List<Any>,
)

data class ResponseMessage(
    val role: String,
    val content: String,
)

data class TextContent(
    val text: String,
    val type: String = "text",
)

data class ImageContent(
    @JsonProperty("image_url")
    val imageUrl: ImageInfo,
    val type: String = "image_url",
)

data class ImageInfo(
    val url: String,
)

data class ChatGPTRequest(
    var model: String,
    var messages: MutableList<RequestMessage>,
) {
    constructor(model: String, imageUrl: String) :
        this(
            model,
            mutableListOf(
                RequestMessage(
                    "user",
                    mutableListOf(TextContent(PROMPT), ImageContent(ImageInfo(imageUrl))),
                ),
            ),
        )
}

data class ChatGPTResponse(
    val choices: List<Choice>,
)

data class Choice(
    var index: Int,
    var message: ResponseMessage,
)
