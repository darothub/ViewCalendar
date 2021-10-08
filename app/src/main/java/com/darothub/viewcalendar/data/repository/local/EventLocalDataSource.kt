package com.darothub.viewcalendar.data.repository.local

import android.util.Log
import com.darothub.viewcalendar.model.Holiday
import com.darothub.viewcalendar.model.HolidayDTO
import com.darothub.viewcalendar.services.local.EventRealmDao
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import io.realm.RealmResults
import io.realm.kotlin.executeTransactionAwait
import io.realm.kotlin.where
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

class EventLocalDataSource @Inject constructor(private val realmConfiguration: RealmConfiguration) : EventRealmDao {
    /**
     * Dispatcher used to run suspendable functions that run Realm transactions. This is needed to
     * confine Realm instances within the same thread as long as the coroutine is running to avoid
     * accessing said instances from different threads and thus (potentially) triggering a thread
     * violation.
     */
    private val monoThreadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    /**
     * [Realm] instance used to fire queries. It must not be used for other than firing queries and
     * has to be closed when no longer in use.
     */
    private val realm = Realm.getInstance(realmConfiguration)
    override suspend fun insertEvents(events: HolidayDTO) {
        withContext(monoThreadDispatcher) {
            runCloseableTransaction(realmConfiguration) { transactionRealm ->
                transactionRealm.insert(events)
            }
        }
    }

    override suspend fun getEvents(from: Int, to: Int): Flow<List<HolidayDTO>> = flow{
        realm.where<HolidayDTO>().between("index", from, to).findAllAsync()
    }

    override fun getAllEvents(): Flow<List<HolidayDTO>> = flow {
        val r = realm.where<HolidayDTO>().findAllAsync()
        r

    }
    override fun getAllEventsTwo(): RealmResults<HolidayDTO> {
        val r = realm.where<HolidayDTO>().findAllAsync()
        return r
    }

    override fun close() {
        realm.close()
    }
    private suspend fun runCloseableTransaction(
        realmConfiguration: RealmConfiguration,
        transaction: (realm: Realm) -> Unit
    ) {
        Realm.getInstance(realmConfiguration).use { realmInstance ->
            realmInstance.executeTransactionAwait(transaction = transaction)
        }
    }
}

