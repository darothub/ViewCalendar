package com.darothub.viewcalendar.data.repository

import android.util.Log
import com.darothub.viewcalendar.data.repository.remote.EventRemoteDataSource
import com.darothub.viewcalendar.model.EventRequest
import com.darothub.viewcalendar.model.Holiday
import com.darothub.viewcalendar.model.HolidayDTO
import com.darothub.viewcalendar.model.UIState
import com.darothub.viewcalendar.services.local.EventRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class EventRepository @Inject constructor(
    private val localDataSource: EventRealmDao,
    private val remoteDataSource: EventRemoteDataSource
) {
    private val TAG by lazy { this::class.qualifiedName!! }
    lateinit var holidayDTO:HolidayDTO
    private var listOfHolidayDTO = ArrayList<HolidayDTO>()
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private suspend fun getRemoteEvent(eventRequest: EventRequest): ArrayList<HolidayDTO>{
        val response = remoteDataSource.getEvents(eventRequest)
        val holidays = response.holidays
        holidays.forEach { (t, u) ->
            holidayDTO = HolidayDTO()
            holidayDTO.date = format.parse(t)?.time
            for (i in u){
                val hol = Holiday(i.name, i.type)
                holidayDTO.holidays.add(hol)
            }
            localDataSource.insertEvents(holidayDTO)
            listOfHolidayDTO.add(holidayDTO)

        }
        localDataSource.close()
       return listOfHolidayDTO
    }
    suspend fun getCachedEvent(eventRequest: EventRequest?=null): List<HolidayDTO> {
        return if (eventRequest == null){
            val h = localDataSource.getAllEventsTwo()
            val b = ArrayList<HolidayDTO>()

            for (i in h){
                b.add(i)
            }
            Log.i(TAG, b.toString())
            b
        } else{
            getRemoteEvent(eventRequest)

        }

    }
}