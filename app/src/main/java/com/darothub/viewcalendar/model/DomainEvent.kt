package com.darothub.viewcalendar.model

import com.darothub.viewcalendar.com.darothub.viewcalendar.model.HolidayDTO



typealias DomainEventMap = Map<String, List<Holiday>>
data class DomainEvent(val error: Boolean=true, val holidays: DomainEventMap?=null, val reason:String?=null){
    lateinit var holidayDTOs: List<HolidayDTO>
    var local = false
}


