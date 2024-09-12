package com.youngsdeveloper.bus_murcia.models

data class PanelItem(
    val stop_id:    Int,
    val line_id: Int,
    val direction: Int,
    val synoptic: String,
    val line_name: String,
    val line_headsign: String,
    val destination_id: Int,
    val destination_name: String,
    val recorded_at_time: String,
    val delay_flag: String,
    val delay_minutes: Int,
    val expected_arrival_in: Int,
    val end_of_line: Boolean,
    val is_arrival_time: Boolean,
)
