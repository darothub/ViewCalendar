package com.darothub.viewcalendar.adapter

import android.view.View
import com.darothub.viewcalendar.databinding.CalendarDayViewLayoutBinding
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.LocalDate

class DayViewContainer(view: View, calendarView: CalendarView, action:(String)->Unit) : ViewContainer(view) {
    var selectedDate: LocalDate? = null
    lateinit var day: CalendarDay // Will be set when this container is bound.
    val binding = CalendarDayViewLayoutBinding.bind(view)
    init {
        view.setOnClickListener {
            if (day.owner == DayOwner.THIS_MONTH) {
                if (selectedDate != day.date) {
                    selectedDate = day.date
                    calendarView.notifyDateChanged(day.date)
                    selectedDate?.let { calendarView.notifyDateChanged(it) }
                    action.invoke(day.date.toString())
//                    updateAdapterForDate(day.date)
                }
            }
        }
    }
}