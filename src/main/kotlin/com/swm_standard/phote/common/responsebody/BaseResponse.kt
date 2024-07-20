package com.swm_standard.phote.common.responsebody

data class BaseResponse<T>(
    val result: String = SuccessCode.SUCCESS.name,
    val status: Int = SuccessCode.SUCCESS.statusCode,
    val msg: String = SuccessCode.SUCCESS.msg,
    val data: T? = null,
)
