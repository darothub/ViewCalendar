package com.darothub.viewcalendar.model

import androidx.annotation.ColorRes
import com.darothub.viewcalendar.R

data class DomainEvent(val name:String, val type:String, @ColorRes var color: Int = 0){
    init {
        color = if (type == "folk"){
            R.color.purple_200
        } else{
            R.color.teal_200
        }
    }
}
