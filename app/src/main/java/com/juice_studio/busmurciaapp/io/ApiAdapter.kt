package com.juice_studio.busmurciaapp.io

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiAdapter {

    companion object{

        fun getApiService(): ApiService {
            return Retrofit
                    .Builder()
                    .baseUrl("https://api.latbus.com/latbusapp/rest/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
        }
    }

}