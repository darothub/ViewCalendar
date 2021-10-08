package com.darothub.viewcalendar.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darothub.viewcalendar.Keys
import com.darothub.viewcalendar.adapter.CalendarDayBinder
import com.darothub.viewcalendar.adapter.EventAdapter
import com.darothub.viewcalendar.adapter.MonthHeaderBinder
import com.darothub.viewcalendar.com.darothub.viewcalendar.utils.show
import com.darothub.viewcalendar.data.viewmodel.EventViewModel
import com.darothub.viewcalendar.databinding.ActivityMainBinding
import com.darothub.viewcalendar.model.EventRequest
import com.darothub.viewcalendar.model.Holiday
import com.darothub.viewcalendar.model.HolidayDTO
import com.darothub.viewcalendar.model.UIState
import com.darothub.viewcalendar.utils.daysOfWeekFromLocale
import com.darothub.viewcalendar.utils.getEvents
import com.darothub.viewcalendar.utils.hideSystemUI
import com.google.android.material.datepicker.MaterialDatePicker
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
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
    val picker by lazy {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val now = Calendar.getInstance()
        builder.build()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
        val picker = builder
            .setTitleText("Select dates")
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()
        picker
    }
    private val viewModel: EventViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI(binding.root)
        activityUiStateListener = this

        binding.rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = eventAdapter
            addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
        }
        eventAdapter.notifyDataSetChanged()

        lifecycleScope.launchWhenCreated {
            viewModel.getEvent()
            viewModel.uiState.collect { state ->
                when (state) {
                    is UIState.Success<*> -> {
                        state.data as List<HolidayDTO>
                        Log.i("Main Act", state.data[0].toString())
                        if (state.data.isNotEmpty()){
                            displayData(state.data)
                        }
                        else{
                            picker.show(supportFragmentManager, picker.toString())
                        }

                    }
                    is UIState.Error -> {
                        showErrorPage("error: String?")
                    }
                    is UIState.Loading -> {
                        loading()
                    }
                    is UIState.Nothing -> {
                        picker.show(supportFragmentManager, picker.toString())
                    }
                }
            }
        }



        picker.addOnNegativeButtonClickListener {
            Log.i("Main", "Negative")
        }
        picker.addOnPositiveButtonClickListener {
            val eventRequest = EventRequest(
                Keys.apiKey(),
                dateFormat.format(it.first), dateFormat.format(it.second)
            )
            lifecycleScope.launch {
                viewModel.getEvent(eventRequest)
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

    override fun showErrorPage(error: String?) {}

    override fun loading() {
        binding.vf.show()
    }

    override fun displayData(data:List<HolidayDTO>) {

        binding.vf.show()
        for (d in data){
            duplicateEventMap[dateFormat.format(d.date)] = d.holidays
            Log.i("Main", duplicateEventMap.toString())
        }
        val currentDate = data[0].date
        Log.i("MainDate", currentDate.toString())

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.of(2019, 2)
        binding.calendar.setup(currentMonth.minusMonths(11), currentMonth.plusMonths(11), daysOfWeek.first())
        binding.calendar.scrollToMonth(currentMonth)

        binding.calendar.dayBinder = CalendarDayBinder(binding.calendar, duplicateEventMap){
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
        binding.vf.displayedChild = 1
    }


}

interface ActivityUiStateListener {
    fun showErrorPage(error: String?)
    fun loading()
    fun displayData(data : List<HolidayDTO>)
}


