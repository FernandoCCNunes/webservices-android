package com.nando.webservices

import org.json.JSONObject

data class JsonObjectResponse(val data: JSONObject?, val statusCode: Int?, val networkTimeMs: Long?, val isSuccessful: Boolean = false, val cause: Throwable?, val message: String?, val localizedMessage: String?)
