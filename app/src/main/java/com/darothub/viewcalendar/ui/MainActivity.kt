package com.darothub.viewcalendar.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darothub.viewcalendar.Keys
import com.darothub.viewcalendar.R
import com.darothub.viewcalendar.ui.adapter.CalendarDayBinder
import com.darothub.viewcalendar.ui.adapter.EventAdapter
import com.darothub.viewcalendar.ui.adapter.MonthHeaderBinder
import com.darothub.viewcalendar.com.darothub.viewcalendar.model.HolidayDTO
import com.darothub.viewcalendar.com.darothub.viewcalendar.utils.hide
import com.darothub.viewcalendar.com.darothub.viewcalendar.utils.show
import com.darothub.viewcalendar.data.viewmodel.EventViewModel
import com.darothub.viewcalendar.databinding.ActivityMainBinding
import com.darothub.viewcalendar.model.EventRequest
import com.darothub.viewcalendar.model.Holiday
import com.darothub.viewcalendar.model.UIState
import com.darothub.viewcalendar.utils.daysOfWeekFromLocale
import com.darothub.viewcalendar.utils.makeFullScreen
import com.google.android.material.datepicker.MaterialDatePicker
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityUiStateListener {
    private lateinit var binding: ActivityMainBinding
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private var selectedDate: LocalDate? = null
    private val eventAdapter = EventAdapter()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val duplicateEventMap = HashMap<String, List<Holiday>>()
    lateinit var activityUiStateListener: ActivityUiStateListener
    private val picker by lazy {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val now = Calendar.getInstance()
        builder.build()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
        val picker = builder
            .setTitleText("Select dates")
            .setTheme(R.style.MaterialDatePickerStyle)
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()
        picker
    }
    lateinit var ldt:LocalDateTime
    private val viewModel: EventViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeFullScreen(binding.root)
        activityUiStateListener = this

        binding.calendarLayout.rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = eventAdapter
            addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
        }
        eventAdapter.notifyDataSetChanged()

        lifecycleScope.launchWhenCreated {
            viewModel.getEvents()
            viewModel.uiState.collect { state ->
                uiStateWatch(state)
            }
        }

        binding.back.setOnClickListener {
            picker.show(supportFragmentManager, picker.toString())
        }


        picker.addOnPositiveButtonClickListener {
            duplicateEventMap.clear()
            val eventRequest = EventRequest(
                Keys.apiKey(),
                dateFormat.format(it.first), dateFormat.format(it.second)
            )
            lifecycleScope.launch {
                viewModel.getEvents(eventRequest)
            }

        }

    }

    private fun uiStateWatch(
        state: UIState,
    ) {
        when (state) {
            is UIState.Success<*> -> {
                state.data as List<HolidayDTO>
                if (state.data.isNotEmpty()) {
                    displayData(state.data)
                } else {
                    picker.show(supportFragmentManager, picker.toString())
                }
            }
            is UIState.Error -> {
                showErrorPage(state.exception)
            }
            is UIState.Loading -> {
                loading()
            }
            is UIState.Nothing -> {
                picker.show(supportFragmentManager, picker.toString())
            }
        }
    }

    private fun updateAdapterForDate(date: String?) {
        if (date != null) {
            eventAdapter.date = date
        }
        eventAdapter.dataList.clear()
        eventAdapter.dataList.addAll(duplicateEventMap[date].orEmpty())
        eventAdapter.notifyDataSetChanged()
    }

    override fun showErrorPage(error: String?) {
        binding.loader.errorText.text = error
        binding.loader.errorText.show()
        binding.loader.progressbar.hide()
        binding.vf.displayedChild = 0
    }

    override fun loading() {
        binding.vf.displayedChild = 0
        binding.loader.progressbar.show()

    }

    override fun displayData(data:List<HolidayDTO>) {

        for (d in data){
            duplicateEventMap[dateFormat.format(d.date)] = d.holidays
        }
        val currentDate = data[0].date

        if (currentDate != null){
            ldt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(currentDate),
                ZoneId.systemDefault()
            )

        }


        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.of(ldt.year, ldt.monthValue)
        binding.calendarLayout.calendar.setup(currentMonth.minusMonths(11), currentMonth.plusMonths(11), daysOfWeek.first())
        binding.calendarLayout.calendar.scrollToMonth(currentMonth)

        binding.calendarLayout.calendar.dayBinder = CalendarDayBinder(binding.calendarLayout.calendar, duplicateEventMap){
            updateAdapterForDate(it)
        }
        binding.calendarLayout.calendar.monthHeaderBinder = MonthHeaderBinder()
        binding.calendarLayout.calendarMonthYearText.text = currentMonth.month.name
        binding.calendarLayout.calendar.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
            binding.calendarLayout.calendarMonthYearText.text = title

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.calendarLayout.calendar.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        binding.calendarLayout.calendarNextMonthImage.setOnClickListener {
            binding.calendarLayout.calendar.findFirstVisibleMonth()?.let {
                binding.calendarLayout.calendar.smoothScrollToMonth(it.yearMonth.next)
            }
        }

        binding.calendarLayout.calendarPreviousMonthImage.setOnClickListener {
            binding.calendarLayout.calendar.findFirstVisibleMonth()?.let {
                binding.calendarLayout.calendar.smoothScrollToMonth(it.yearMonth.previous)
            }
        }
        binding.vf.displayedChild = 1
    }


}

interface ActivityUiStateListener {
    fun showErrorPage(error: String?)
    fun loading()
    fun displayData(data : List<HolidayDTO>)
}


