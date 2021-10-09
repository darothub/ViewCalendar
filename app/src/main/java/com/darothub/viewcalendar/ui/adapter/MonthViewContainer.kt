package com.darothub.viewcalendar.ui.adapter

import android.view.View
import com.darothub.viewcalendar.databinding.CalendarViewHeaderBinding
import com.kizitonwose.calendarview.ui.ViewContainer


class MonthViewContainer(view: View) : ViewContainer(view) {
    val legendLayout = CalendarViewHeaderBinding.bind(view).legendLayout.root
}