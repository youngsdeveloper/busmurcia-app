package com.juice_studio.busmurciaapp.models

data class Stop(
    val id: Integer,
    val name: String,
    val lines: List<Line>
){
    fun getLinesByRoute():Map<Int, List<Line>>{
        return lines.groupBy { line -> line.route }
    }

    fun getRoutesLabel():List<Int>{
        return lines.map{line -> line.route}.distinct()
    }

    fun getRoutes():List<Route>{
        val routes = mutableListOf<Route>()
        val lines_by_route = getLinesByRoute()
        for (route in lines_by_route){
            val route = Route(route.key, route.value)
            routes.add(route)
        }
        return routes
    }
}
