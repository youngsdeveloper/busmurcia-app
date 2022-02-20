package com.juice_studio.busmurciaapp.models

data class RealTimeData(
        var status:String,
        var status_color: Int,
        var llegada_estimada: String,
        var linea_cabecera: String,
        var only_status: Boolean=false,
        var status_min:String?=null
){

}
