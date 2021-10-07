package com.darothub.viewcalendar.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects


typealias DomainEventMap = Map<String, List<Holiday>>
data class RemoteResponse(val error: Boolean, val holidays: DomainEventMap)
open class HolidayDTO : RealmObject(){
    var date: Long? = null
    var holidays: RealmList<Holiday> = RealmList()

    override fun toString(): String {
        return "${this.date} = ${this.holidays}"

    }
}
