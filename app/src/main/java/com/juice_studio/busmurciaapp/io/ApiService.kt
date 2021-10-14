package com.juice_studio.busmurciaapp.io

import com.juice_studio.busmurciaapp.models.Stop
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("nearestStops")
    suspend fun getStopsByCoordinates(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double):Response<List<Stop>>
}