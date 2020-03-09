package com.nando.webservicesexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.nando.webservices.WebServicesConfig
import com.nando.webservices.WebService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WebServicesConfig.debug = true
        getPlants()
    }

    private fun getPlants() {
        val url = "https://bo.eazflora.tetrapi.org/api/species"


        val params: MutableMap<String, String> = mutableMapOf()
        params["page"] = "2"
        val request = WebService(this).post(url, params)
        request.fetchJsonObject {
            Log.d("WebServices", "statusCode -> ${it.statusCode}")
            Log.d("WebServices", "isSuccessful -> ${it.isSuccessful}")
            Log.d("WebServices", "networkTimeMs -> ${it.networkTimeMs}")
            if (it.isSuccessful) {
                if (it.data != null) {
                    with(it.data!!) {
                        Log.d("WebServices", "Count -> ${getJSONObject("data").getInt("total")}")
                    }
                }
            }
        }
    }
}
