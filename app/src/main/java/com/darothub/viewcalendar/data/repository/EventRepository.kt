package com.darothub.viewcalendar.data.repository

import android.util.Log
import com.darothub.viewcalendar.data.repository.remote.EventRemoteDataSource
import com.darothub.viewcalendar.model.*
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
    private suspend fun getRemoteEvent(eventRequest: EventRequest): RemoteResponse{
        val response = remoteDataSource.getEvents(eventRequest)
        val holidays = response.holidays
        if (!response.error){
            holidays?.forEach { (t, u) ->
                holidayDTO = HolidayDTO()
                holidayDTO.date = format.parse(t)?.time

                for (i in u){
                    holidayDTO.holidays.add(i.colorThis())
                }

                localDataSource.insertEvents(holidayDTO)
                listOfHolidayDTO.add(holidayDTO)
            }
            response.holidayDTOs = listOfHolidayDTO
            return response
        }
        Log.i(TAG, response.toString())
        return response

    }
    suspend fun getCachedEvent(eventRequest: EventRequest?=null): RemoteResponse {
        return if (eventRequest == null){
            val realmHoliday = localDataSource.getAllEventsTwo()
            val newHoliday = ArrayList<HolidayDTO>()
            for (i in realmHoliday){
                newHoliday.add(i)
            }
            RemoteResponse().also {
                it.holidayDTOs = newHoliday
            }
        } else{
            getRemoteEvent(eventRequest)
        }

    }
}