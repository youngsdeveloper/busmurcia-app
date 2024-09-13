package com.youngsdeveloper.bus_murcia.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stop(
    val id: Int,
    val name: String,
    val lines: List<Line>? = listOf(),
    val city: String?,
    val order: Int,
    val latitude: Double,
    val longitude: Double
):Parcelable{
    fun getLinesByRoute():Map<Int, List<Line>>?{
        if(lines==null){
            return null;
        }
        return lines.groupBy { line -> line.route }
    }

    fun getRoutesLabel():List<Int>?{
        if(lines==null){
            return null;
        }
        return lines.map{line -> line.route}.distinct()
    }

    fun getRoutes():List<Route>{
        val routes = mutableListOf<Route>()
        val lines_by_route = getLinesByRoute()
        if(lines_by_route==null){
            return mutableListOf();
        }
        if(lines_by_route.isEmpty()){
            return mutableListOf();
        }
        for (route in lines_by_route){
            val route = Route(route.key, route.value, null)
            routes.add(route)
        }
        return routes
    }

}
