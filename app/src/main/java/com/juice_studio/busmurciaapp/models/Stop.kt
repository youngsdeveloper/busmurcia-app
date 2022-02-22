package com.juice_studio.busmurciaapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stop(
    val id: Integer,
    val name: String,
    val lines: List<Line>,
    val city: String,
    val order: Int,
    val latitude: Double,
    val longitude: Double
):Parcelable{
    fun getLinesByRoute():Map<Int, List<Line>>{
        return lines.groupBy { line -> line.route }
    }

    fun getRoutesLabel():List<Int>{
        return lines.map{line -> line.route}.distinct()
    }

    fun getRoutes():List<Route>{
        val routes = mutableListOf<Route>()
        val lines_by_route = getLinesByRoute()
        if(lines_by_route.isEmpty()){
            return mutableListOf();
        }
        for (route in lines_by_route){
            val route = Route(route.key, route.value, null)
            routes.add(route)
        }
        return routes
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stop

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
