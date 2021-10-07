package com.darothub.viewcalendar.model

import androidx.annotation.ColorRes
import com.darothub.viewcalendar.R
import io.realm.RealmObject
open class Holiday(var name: String="", var type: String="", @ColorRes var color: Int = 0) : RealmObject() {

    init {
        color = if (type == "folk"){
            R.color.purple_200
        } else{
            R.color.teal_200
        }
    }
}
