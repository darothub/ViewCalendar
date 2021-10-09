package com.darothub.viewcalendar.services.remote

import com.darothub.viewcalendar.model.EventRequest
import com.darothub.viewcalendar.model.DomainEvent
import retrofit2.http.Body
import retrofit2.http.POST

interface EventServices {
    @POST("holidays")
    suspend fun getHolidays(@Body eventRequest:EventRequest ): DomainEvent
}