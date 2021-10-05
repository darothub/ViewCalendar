package com.darothub.viewcalendar.adapter

import android.view.View
import com.darothub.viewcalendar.R
import com.darothub.viewcalendar.utils.setTextColorRes
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import java.time.LocalDate

class CalendarDayBinder(private val calendarView: CalendarView) : DayBinder<DayViewContainer> {
    override fun bind(container: DayViewContainer, day: CalendarDay) {
        container.day = day
        val textView = container.binding.calendarDayText
        val layout = container.binding.calendarDayLayout
        textView.text = day.date.dayOfMonth.toString()
        val topView = container.binding.calendarDayTop
        val bottomView = container.binding.calendarDayBottom
        topView.background = null
        bottomView.background = null
        if (day.owner == DayOwner.THIS_MONTH) {
            textView.setTextColorRes(R.color.white)
            layout.setBackgroundResource(if (container.selectedDate == day.date) R.drawable.selected_bg else 0)

//
//            val flights = flights[day.date]
//            if (flights != null) {
//                if (flights.count() == 1) {
//                    flightBottomView.setBackgroundColor(view.context.getColorCompat(flights[0].color))
//                } else {
//                    flightTopView.setBackgroundColor(view.context.getColorCompat(flights[0].color))
//                    flightBottomView.setBackgroundColor(view.context.getColorCompat(flights[1].color))
//                }
//            }
        } else {
            textView.setTextColorRes(R.color.colorPrimary)
            layout.background = null
        }
    }

    override fun create(view: View) = DayViewContainer(view, calendarView)

}