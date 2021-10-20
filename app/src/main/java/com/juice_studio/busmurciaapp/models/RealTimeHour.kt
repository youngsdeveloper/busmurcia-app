package com.juice_studio.busmurciaapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

private const val BUS_ADELANTADO = "adelantado"
private const val BUS_RETRASADO = "retrasado"


@Parcelize
data class RealTimeHour(
        val line_id: Integer,
        val stop_id: Integer,
        val synoptic: String,
        val delay_string: String,
        val delay_minutes: Integer,
        val real_time: String
) : Parcelable{

    fun isAdelantado():Boolean{
        return delay_string == BUS_ADELANTADO
    }

    fun isRetrasado():Boolean{
        return delay_string == BUS_RETRASADO
    }

    fun isEnHora():Boolean{
        return delay_minutes.toInt() == 0
    }

}