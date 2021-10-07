package com.darothub.viewcalendar.data.repository.remote

import com.darothub.viewcalendar.model.EventRequest
import com.darothub.viewcalendar.model.RemoteResponse
import com.darothub.viewcalendar.services.local.EventRealmDao
import com.darothub.viewcalendar.services.remote.EventServices
import java.util.*
import javax.inject.Inject

class EventRemoteDataSource @Inject constructor(private val eventServices: EventServices) {
    suspend fun getEvents(eventRequest: EventRequest): RemoteResponse {
       return eventServices.getHolidays(eventRequest)
    }
}