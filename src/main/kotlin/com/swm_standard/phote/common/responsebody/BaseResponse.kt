package com.swm_standard.phote.common.responsebody

data class BaseResponse<T>(
    val result: String = ResultCode.SUCCESS.name,
    val status: Int = ResultCode.SUCCESS.statusCode,
    val msg: String = ResultCode.SUCCESS.msg,
    val data: T? = null,
)