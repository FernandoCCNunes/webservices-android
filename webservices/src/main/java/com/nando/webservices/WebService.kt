package com.nando.webservices

import android.content.Context
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class WebService(private val context: Context) {

    var executable: Executable? = null

    fun get(url: String, params: MutableMap<String, String>? = null, headers: MutableMap<String, String>? = null): Executable {
        executable = Executable(context, Request.Method.GET, url, params, headers)
        return executable!!
    }

    fun post(url: String, params: MutableMap<String, String>? = null, headers: MutableMap<String, String>? = null): Executable {
        executable = Executable(context, Request.Method.POST, url, params, headers)
        return executable!!
    }

    fun put(url: String, params: MutableMap<String, String>? = null, headers: MutableMap<String, String>? = null): Executable {
        executable = Executable(context, Request.Method.PUT, url, params, headers)
        return executable!!
    }

    fun patch(url: String, params: MutableMap<String, String>? = null, headers: MutableMap<String, String>? = null): Executable {
        executable = Executable(context, Request.Method.PATCH, url, params, headers)
        return executable!!
    }

    fun delete(url: String, params: MutableMap<String, String>? = null, headers: MutableMap<String, String>? = null): Executable {
        executable = Executable(context, Request.Method.DELETE, url, params, headers)
        return executable!!
    }

    class Executable(context: Context, val method: Int, var url: String, var params: MutableMap<String, String>? = null, var headers: MutableMap<String, String>? = null) {

        private val queue: RequestQueue = Volley.newRequestQueue(context)
        private val res = MutableResponse()
        private var request: StringRequest? = null

        private fun getRequest(onSuccess: (String) -> Unit, onError: (VolleyError) -> Unit): StringRequest {
            return object: StringRequest(method, url, { onSuccess(it) }, { onError(it) }) {
                override fun getParams(): MutableMap<String, String> = this@Executable.params ?: super.getParams()
                override fun getHeaders(): MutableMap<String, String> = this@Executable.params ?: super.getHeaders()

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    Log.d("WebServices", "HERE 1")
                    res.apply {
                        statusCode = response?.statusCode
                        networkTimeMs = response?.networkTimeMs
                        isSuccessful = true
                    }
                    Log.d("WebServices", "res -> $res")
                    return super.parseNetworkResponse(response)
                }

                override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                    res.apply {
                        statusCode = volleyError?.networkResponse?.statusCode
                        networkTimeMs = volleyError?.networkTimeMs ?: volleyError?.networkResponse?.networkTimeMs
                        isSuccessful = false
                        cause = volleyError?.cause
                        message = volleyError?.message
                        localizedMessage = volleyError?.localizedMessage
                    }
                    return super.parseNetworkError(volleyError)
                }

                override fun getUrl(): String {
                    if (method == Method.GET || method == Method.DELETE) {
                        return super.getUrl().appendParams()
                    }
                    return super.getUrl()
                }

                private fun String.appendParams(): String {
                    var string = this
                    params.forEach {
                        string += if (!string.contains("?")) "?${it.key}=${it.value}"
                        else "&${it.key}=${it.value}"
                    }
                    return string
                }
            }
        }

        private fun addExtraOptions() {
            request!!.retryPolicy = DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            queue.add(request)
        }


        fun fetchString(callback: (StringResponse) -> Unit)  {
            fun onSuccess(it: String) {
                Log.d("WebServices", "Success -> $it")
                with(res) {
                    callback(StringResponse(it, statusCode, networkTimeMs, isSuccessful, cause, message, localizedMessage))
                }
            }

            fun onError(it: VolleyError) {
                with(res) {
                    callback(StringResponse(null, statusCode, networkTimeMs, isSuccessful, cause, message, localizedMessage))
                }
            }

            request = getRequest({ onSuccess(it) }, { onError(it) })

            addExtraOptions()
        }

        fun fetchJsonObject(callback: (JsonObjectResponse) -> Unit)  {
            fun onSuccess(it: String) {
                Log.d("WebServices", "Success -> $it")
                var data: JSONObject? = null
                try {
                    data = JSONObject(it)
                } catch (ex: Exception) {
                    Log.d("WebServices", "Failed to parse string response to JSON array")
                }
                with(res) {
                    callback(JsonObjectResponse(data, statusCode, networkTimeMs, isSuccessful, cause, message, localizedMessage))
                }
            }

            fun onError(it: VolleyError) {
                with(res) {
                    callback(JsonObjectResponse(null, statusCode, networkTimeMs, isSuccessful, cause, message, localizedMessage))
                }
            }

            request = getRequest({ onSuccess(it) }, { onError(it) })

            addExtraOptions()
        }

        fun fetchJsonArray(callback: (JsonArrayResponse) -> Unit) {
            fun onSuccess(it: String) {
                var data: JSONArray? = null
                try {
                    data = JSONArray(it)
                } catch (ex: Exception) {
                    Log.d("WebServices", "Failed to parse string response to JSON array")
                }
                with(res) {
                    callback(JsonArrayResponse(data, statusCode, networkTimeMs, isSuccessful, cause, message, localizedMessage))
                }
            }

            fun onError(it: VolleyError) {
                with(res) {
                    callback(JsonArrayResponse(null, statusCode, networkTimeMs, isSuccessful, cause, message, localizedMessage))
                }
            }

            request = getRequest({ onSuccess(it) }, { onError(it) })

            addExtraOptions()
        }

        fun cancel() {
            request?.run {
                cancel()
                Log.d("WebServices", "Request canceled")
            }
        }
    }

    private open class MutableResponse {
        var statusCode: Int? = null
        var networkTimeMs: Long? = null
        var isSuccessful: Boolean = false
        var cause: Throwable? = null
        var message: String? = null
        var localizedMessage: String? = null


        override fun toString(): String {
            return "statusCode -> ${statusCode?: "null"}, networkTimeMs -> ${networkTimeMs?: "null"}, isSuccessful -> $isSuccessful"
        }
    }

    data class StringResponse(val data: String?, val statusCode: Int?, val networkTimeMs: Long?, val isSuccessful: Boolean?, val cause: Throwable?, val message: String?, val localizedMessage: String?)
    data class JsonObjectResponse(val data: JSONObject?, val statusCode: Int?, val networkTimeMs: Long?, val isSuccessful: Boolean, val cause: Throwable?, val message: String?, val localizedMessage: String?)
    data class JsonArrayResponse(val data: JSONArray?, val statusCode: Int?, val networkTimeMs: Long?, val isSuccessful: Boolean?, val cause: Throwable?, val message: String?, val localizedMessage: String?)
}