package com.darothub.viewcalendar.model

import io.realm.RealmList
import io.realm.RealmObject


typealias DomainEventMap = Map<String, List<Holiday>>
data class RemoteResponse(val error: Boolean=true, val holidays: DomainEventMap?=null, val reason:String?=null){
    lateinit var holidayDTOs: List<HolidayDTO>
    var local = false
}
open class HolidayDTO : RealmObject(){
    var date: Long? = null
    var holidays: RealmList<Holiday> = RealmList()

    override fun toString(): String {
        return "${this.date} = ${this.holidays}"
    }
}

