package com.juice_studio.busmurciaapp.models

data class StopRoute(
    val id:String,
    val name:String,
    val route:Int,
    val synoptic:String,
    val direction: Int,
    val headsign:String,
    val stops: List<Stop>
)
