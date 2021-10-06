package com.darothub.viewcalendar.services

import com.darothub.viewcalendar.model.DomainEvents
import com.darothub.viewcalendar.model.EventRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface EventServices {
    @POST("holidays")
    suspend fun getHolidays(@Body eventRequest:EventRequest ): DomainEvents
}