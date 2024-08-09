package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    @JsonIgnore
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @JsonIgnore
    var modifiedAt: LocalDateTime? = null

    @JsonIgnore
    val deletedAt: LocalDateTime? = null
}
