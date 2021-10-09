package com.darothub.viewcalendar.ui.adapter

import android.view.View
import com.darothub.viewcalendar.R
import com.darothub.viewcalendar.model.Holiday
import com.darothub.viewcalendar.utils.getColorCompat
import com.darothub.viewcalendar.utils.setTextColorRes
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder

class CalendarDayBinder(
    private val calendarView: CalendarView,
    private val duplicateEventMap :HashMap<String, List<Holiday>>,
    private val action:(String)->Unit) : DayBinder<DayViewContainer> {
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
            layout.setBackgroundResource(if (DayViewContainer.selectedDate == day.date) R.drawable.selected_bg else 0)

            val events = duplicateEventMap[day.date.toString()]
            if (events != null) {
                if (events.count() == 1) {
                    bottomView.setBackgroundColor(calendarView.context.getColorCompat(events[0].color))
                } else {
                    topView.setBackgroundColor(calendarView.context.getColorCompat(events[0].color))
                    bottomView.setBackgroundColor(calendarView.context.getColorCompat(events[1].color))
                }
            }
        } else {
            textView.setTextColorRes(R.color.colorPrimary)
            layout.background = null
        }
    }

    override fun create(view: View) = DayViewContainer(view, calendarView){
        action.invoke(it)
    }

}