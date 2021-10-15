package com.juice_studio.busmurciaapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RealTimeHour(
        val line_id: Integer,
        val synoptic: String,
        val delay_string: String,
        val delay_minutes: Integer,
        val real_time: String
) : Parcelable