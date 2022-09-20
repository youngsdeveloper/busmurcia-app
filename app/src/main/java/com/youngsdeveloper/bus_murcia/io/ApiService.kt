package com.youngsdeveloper.bus_murcia.io

import com.youngsdeveloper.bus_murcia.models.RealTimeHour
import com.youngsdeveloper.bus_murcia.models.StopRoute
import com.youngsdeveloper.bus_murcia.models.Stop
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("nearestStops")
    suspend fun getStopsByCoordinates(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double):Response<List<Stop>>

    @GET("realTimeMultiStop")
    suspend fun getRealTimeHours(@Query("stop[]") stops: List<String>, @Query("line_compound_id[]") line_compound_id: List<String>):Response<List<RealTimeHour>>


    @GET("line")
    suspend fun getLineStops(@Query("route") route: Int):Response<List<StopRoute>>


}