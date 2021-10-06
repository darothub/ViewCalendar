package com.darothub.viewcalendar.model

data class EventRequest (
    val apiKey: String,
    val startDate: String,
    val endDate: String
)