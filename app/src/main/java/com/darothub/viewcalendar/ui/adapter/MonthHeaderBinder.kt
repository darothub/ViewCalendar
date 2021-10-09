package com.darothub.viewcalendar.ui.adapter

import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.darothub.viewcalendar.R
import com.darothub.viewcalendar.utils.daysOfWeekFromLocale
import com.darothub.viewcalendar.utils.setTextColorRes
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import java.time.format.TextStyle
import java.util.*

class MonthHeaderBinder : MonthHeaderFooterBinder<MonthViewContainer> {
    private val daysOfWeek = daysOfWeekFromLocale()
    override fun bind(container: MonthViewContainer, month: CalendarMonth) {
        if (container.legendLayout.tag == null) {
            container.legendLayout.tag = month.yearMonth
            container.legendLayout
            container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                tv.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    .toUpperCase(Locale.ENGLISH)
                tv.setTextColorRes(R.color.white)
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            }
            month.yearMonth
        }
    }

    override fun create(view: View) = MonthViewContainer(view)
}