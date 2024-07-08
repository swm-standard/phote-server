package com.swm_standard.phote.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class WorkbookCreationRequest(
    @field:NotBlank
    @JsonProperty("title")
    private val _title: String?,

    @JsonProperty("description")
    private val _description: String?,
) {
    val title: String get() = _title!!

    val description: String get() = _description ?: ""
}

data class WorkbookCreationResponse(
    val id: UUID
)
