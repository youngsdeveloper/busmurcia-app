package com.youngsdeveloper.bus_murcia.extensions


fun String.pluralize(cant: Int):String{
    if(cant>1){
        return this + "s"
    }
    return this
}