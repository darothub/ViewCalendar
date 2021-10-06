package com.darothub.viewcalendar.utils

import android.widget.TextView
import androidx.annotation.ColorRes
import com.darothub.viewcalendar.R
import com.darothub.viewcalendar.model.DomainEvent
import com.darothub.viewcalendar.model.DomainEvents
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.HashMap

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    // Only necessary if firstDayOfWeek != DayOfWeek.MONDAY which has ordinal 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

fun getEvents():Map<String, List<DomainEvent>>{
    val hashMap = HashMap<String, List<DomainEvent>>()
    val firstList = listOf<DomainEvent>(
        DomainEvent("Küünlapäev ehk pudrupäev", "folk", R.color.purple_200)
    )
    val secondList = listOf<DomainEvent>(
        DomainEvent("Luuvalupäev", "folk", R.color.purple_200)
    )
    val thirdList = listOf<DomainEvent>(
        DomainEvent( "Talvine peetripäev", "folk", R.color.purple_200)
    )
    val fourthList = listOf<DomainEvent>(
        DomainEvent("Iseseisvuspäev", "public", R.color.colorSecondary),
        DomainEvent("Talvine madisepäev", "folk", R.color.purple_200)
    )
    hashMap["2021-10-02"] = firstList
    hashMap["2021-10-12"] = secondList
    hashMap["2021-10-13"] = thirdList
    hashMap["2021-10-14"] = fourthList
    return hashMap
}