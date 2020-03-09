package com.nando.webservices

data class StringResponse(val data: String?, val statusCode: Int?, val networkTimeMs: Long?, val isSuccessful: Boolean = false, val cause: Throwable?, val message: String?, val localizedMessage: String?)
    