package com.darothub.viewcalendar.com.darothub.viewcalendar.model

import com.darothub.viewcalendar.model.Holiday
import io.realm.RealmList
import io.realm.RealmObject

open class HolidayDTO : RealmObject(){
    var date: Long? = null
    var holidays: RealmList<Holiday> = RealmList()

    override fun toString(): String {
        return "${this.date} = ${this.holidays}"
    }
}