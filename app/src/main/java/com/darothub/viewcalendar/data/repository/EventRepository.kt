package com.darothub.viewcalendar.data.repository

import android.util.Log
import com.darothub.viewcalendar.com.darothub.viewcalendar.model.HolidayDTO
import com.darothub.viewcalendar.data.repository.remote.EventRemoteDataSource
import com.darothub.viewcalendar.model.*
import com.darothub.viewcalendar.services.local.EventRealmDao
import io.realm.RealmResults
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
    suspend fun getRemoteEvent(eventRequest: EventRequest): DomainEvent {
        var response: DomainEvent?
        val from = format.parse(eventRequest.startDate)?.time
        val to = format.parse(eventRequest.endDate)?.time
        val realmHoliday = getCachedEvent()
        var last:HolidayDTO?=null

        if (realmHoliday.holidayDTOs.isNotEmpty()){
            last = realmHoliday.holidayDTOs.last()
        }

        response = if(last != null && last.date!! < to!!){
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
    fun getCachedEvent(): DomainEvent {
        val realmHoliday = localDataSource.fetchAllLocalData()
        val newHoliday = extractHolidaysFromRealmResults(realmHoliday)
        return DomainEvent(error = false).apply {
            holidayDTOs = newHoliday
        }

    }
    private suspend fun getCachedEvent(from:Long?, to:Long?): DomainEvent? {
        val realmHoliday = localDataSource.getEvents(from, to)
        Log.i(TAG, "${realmHoliday.toList()}")
        if (realmHoliday.asJSON().length == 2){
            return null
        }
        val newHoliday = extractHolidaysFromRealmResults(realmHoliday)
        return DomainEvent(error = false).apply {
            holidayDTOs = newHoliday
            local = true
        }

    }

    private fun extractHolidaysFromRealmResults(realmHoliday: RealmResults<HolidayDTO>): ArrayList<HolidayDTO> {
        val newHoliday = ArrayList<HolidayDTO>()
        for (i in realmHoliday) {
            newHoliday.add(i)
        }
        return newHoliday
    }
}