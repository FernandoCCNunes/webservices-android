package com.nando.webservicesexample

import android.app.Activity
import android.os.Bundle
import com.nando.webservices.WebService

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val baseUrl = "http://3a37e174.ngrok.io/api"
        val cams_v1 = "/v1/cams/"

        val params: MutableMap<String, String> = mutableMapOf()
        params["oi"] = "Oi"
        WebService(this).get("$baseUrl$cams_v1", params).fetchJsonArray {

        }
    }
}
