package com.example.studenth.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // If you are running the app on the Android Emulator and your server on the same machine,
    // use "http://10.0.2.2/".
    // If you are running on a physical device, replace "10.0.2.2" with your computer's
    // local IP address (e.g., "http://192.168.1.5/").
    // Make sure to add your subfolder if your PHP scripts are not in the root directory
    // (e.g., "http://10.0.2.2/studenthub/").
    private const val BASE_URL = "http://10.0.2.2/studenthub/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
