package com.nando.webservices

import org.json.JSONArray

data class JsonArrayResponse(val data: JSONArray?, val statusCode: Int?, val networkTimeMs: Long?, val isSuccessful: Boolean, val cause: Throwable?, val message: String?, val localizedMessage: String?)
