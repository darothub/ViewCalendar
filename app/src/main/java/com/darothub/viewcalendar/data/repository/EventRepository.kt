package com.darothub.viewcalendar.data.repository

import android.util.Log
import com.darothub.viewcalendar.data.repository.remote.EventRemoteDataSource
import com.darothub.viewcalendar.model.*
import com.darothub.viewcalendar.services.local.EventRealmDao
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class EventRepository @Inject constructor(
    private val localDataSource: EventRealmDao,
    private val remoteDataSource: EventRemoteDataSource
) {
    private val TAG by lazy { this::class.qualifiedName!! }

    private var listOfHolidayDTO = ArrayList<HolidayDTO>()
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    suspend fun getRemoteEvent(eventRequest: EventRequest): RemoteResponse {
        var response: RemoteResponse?
        val from = format.parse(eventRequest.startDate).time
        val to = format.parse(eventRequest.endDate).time
        val realmHoliday = getCachedEvent()
        var last:HolidayDTO?=null
        if (realmHoliday.holidayDTOs.isNotEmpty()){
            last = realmHoliday.holidayDTOs.last()
            Log.i("LAST", "${last}")
        }

        response = if(last != null && last.date!! < to){
            remoteDataSource.getEvents(eventRequest)
        } else{
            getCachedEvent(from, to) ?:  remoteDataSource.getEvents(eventRequest)
        }

        val holidays = response.holidays
        if (response.local){
            return response
        }
        if (!response.error && !response.local) {
            holidays?.forEach { (t, u) ->
                val holidayDTO = HolidayDTO()
                holidayDTO.date = format.parse(t)?.time
                for (i in u) {
                    holidayDTO.holidays.add(i.colorThis())
                    localDataSource.insertEvents(holidayDTO)
                }

                listOfHolidayDTO.add(holidayDTO)
            }
            response.holidayDTOs = listOfHolidayDTO
            return response
        }
        return response

    }
    suspend fun getCachedEvent(eventRequest: EventRequest?=null): RemoteResponse {
        return if (eventRequest == null){
            val realmHoliday = localDataSource.getAllEventsTwo()
            val newHoliday = ArrayList<HolidayDTO>()
            for (i in realmHoliday){
                newHoliday.add(i)
            }
            RemoteResponse(error = false).also {
                it.holidayDTOs = newHoliday
            }
        } else{
            getRemoteEvent(eventRequest)
        }

    }
    private suspend fun getCachedEvent(from:Long, to:Long): RemoteResponse? {
        val realmHoliday = localDataSource.getEvents(from, to)
        Log.i(TAG, realmHoliday.asJSON())
        if (realmHoliday.asJSON().length == 2){
            return null
        }
        val newHoliday = ArrayList<HolidayDTO>()
        for (i in realmHoliday){
            newHoliday.add(i)
        }
        return RemoteResponse(error = false).apply {
            holidayDTOs = newHoliday
            local = true
        }

    }
}