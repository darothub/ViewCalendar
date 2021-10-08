package com.darothub.viewcalendar.model

import android.util.Log
import androidx.annotation.ColorRes
import com.darothub.viewcalendar.R
import io.realm.RealmObject
import java.io.Serializable

open class Holiday(
    var name: String="",
    var type: String=""
) : RealmObject(), Serializable {
    @ColorRes var color: Int = 0

    fun colorThis(): Holiday{
        color = if (type == "folk") R.color.myLightMintGreen else R.color.myMintGreen
        return this
    }

    override fun toString(): String {
        return "{${this.name} ${this.type} ${this.color}}"
    }
}

