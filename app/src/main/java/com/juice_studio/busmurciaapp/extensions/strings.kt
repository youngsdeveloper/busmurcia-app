package com.juice_studio.busmurciaapp.extensions

import android.content.res.Resources


fun String.pluralize(cant: Int):String{
    if(cant>1){
        return this + "s"
    }
    return this
}