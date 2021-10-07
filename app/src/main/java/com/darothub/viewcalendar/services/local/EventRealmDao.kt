package com.darothub.viewcalendar.services.local
import com.darothub.viewcalendar.model.HolidayDTO
import kotlinx.coroutines.flow.Flow
import java.io.Closeable
import java.util.*

/**
 * Data Access Object interface used to gain access to Realm.
 *
 * It implements [Closeable] to allow proper Realm instance housekeeping linked to handling the
 * Android activity/fragment lifecycle.
 */
interface EventRealmDao : Closeable {
    suspend fun insertEvents(events: HolidayDTO){}
    suspend fun getEvents(from: Int, to:Int): Flow<List<HolidayDTO>>
    suspend fun getAllEvents(): Flow<List<HolidayDTO>>
}