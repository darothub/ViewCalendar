package com.darothub.viewcalendar.model

import androidx.annotation.ColorRes
import com.darothub.viewcalendar.R
import io.realm.RealmObject
open class Holiday(
    var name: String="",
    var type: String="",
    @ColorRes var color: Int = if (type == "folk") R.color.teal_200 else R.color.purple_200) : RealmObject() {

}
