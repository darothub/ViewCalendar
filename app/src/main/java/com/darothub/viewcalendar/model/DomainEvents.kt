package com.darothub.viewcalendar.model

data class DomainEvents(val error: Boolean, val holidays: Map<String, List<DomainEvent>>)
