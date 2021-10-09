package com.darothub.viewcalendar.data.repository.remote

import com.darothub.viewcalendar.model.EventRequest
import com.darothub.viewcalendar.model.DomainEvent
import com.darothub.viewcalendar.services.remote.EventServices
import javax.inject.Inject

class EventRemoteDataSource @Inject constructor(private val eventServices: EventServices) {
    suspend fun getEvents(eventRequest: EventRequest): DomainEvent {
       return eventServices.getHolidays(eventRequest)
    }
}