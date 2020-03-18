package com.nando.webservicesexample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.nando.webservices.WebService

class MainActivity : Activity() {

    private val TAG = "WebServices"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val baseUrl = "http://44e7ecd0.ngrok.io/api"
        val cams_v1 = "/v1/cams/"

        val params: MutableMap<String, String> = mutableMapOf()
        params["oi"] = "Oi"
        WebService(this).get("$baseUrl$cams_v1", params).fetchJsonArray {
            Log.d(TAG, "Request completed")
            if (it.isSuccessful) {
                Log.d(TAG, "Request successfull")
                Log.d(TAG, "data -> ${ it.data}")
            } else {
                Log.d(TAG, "Request failed")
                Log.d(TAG, "message -> ${it.message}")
            }
        }
    }
}
