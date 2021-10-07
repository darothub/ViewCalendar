package com.darothub.viewcalendar.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darothub.viewcalendar.Keys
import com.darothub.viewcalendar.adapter.CalendarDayBinder
import com.darothub.viewcalendar.adapter.EventAdapter
import com.darothub.viewcalendar.adapter.MonthHeaderBinder
import com.darothub.viewcalendar.data.viewmodel.EventViewModel
import com.darothub.viewcalendar.databinding.ActivityMainBinding
import com.darothub.viewcalendar.model.EventRequest
import com.darothub.viewcalendar.utils.daysOfWeekFromLocale
import com.darothub.viewcalendar.utils.getEvents
import com.darothub.viewcalendar.utils.hideSystemUI
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private var selectedDate: LocalDate? = null
    private val eventAdapter = EventAdapter()
    private val eventList by lazy {
        getEvents()
    }
    private val viewModel: EventViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI(binding.root)
//        eventAdapter.setDomainEvents()
        binding.rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = eventAdapter
            addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
        }
        eventAdapter.notifyDataSetChanged()
        val daysOfWeek = daysOfWeekFromLocale()
        val eventRequest = EventRequest(
            Keys.apiKey(),
            "2019-02-01", "2019-02-28"
        )
        lifecycleScope.launchWhenCreated {
            viewModel.getEvent(eventRequest)
        }

        val currentMonth = YearMonth.now()
        binding.calendar.setup(currentMonth.minusMonths(11), currentMonth.plusMonths(11), daysOfWeek.first())
        binding.calendar.scrollToMonth(currentMonth)

        binding.calendar.dayBinder = CalendarDayBinder(binding.calendar){
            updateAdapterForDate(it)
        }
        binding.calendar.monthHeaderBinder = MonthHeaderBinder()

        binding.calendar.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
            binding.calendarMonthYearText.text = title

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.calendar.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        binding.calendarNextMonthImage.setOnClickListener {
            binding.calendar.findFirstVisibleMonth()?.let {
                binding.calendar.smoothScrollToMonth(it.yearMonth.next)
            }
        }

        binding.calendarPreviousMonthImage.setOnClickListener {
            binding.calendar.findFirstVisibleMonth()?.let {
                binding.calendar.smoothScrollToMonth(it.yearMonth.previous)
            }
        }

    }
    private fun updateAdapterForDate(date: String?) {
        if (date != null) {
            eventAdapter.date = date
        }
        eventAdapter.dataList.clear()
        eventAdapter.dataList.addAll(eventList[date].orEmpty())
        eventAdapter.notifyDataSetChanged()
    }


}