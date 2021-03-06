package com.darothub.viewcalendar.data.repository.local

import com.darothub.viewcalendar.com.darothub.viewcalendar.model.HolidayDTO
import com.darothub.viewcalendar.services.local.EventRealmDao
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.kotlin.executeTransactionAwait
import io.realm.kotlin.where
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
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
                transactionRealm.insertOrUpdate(events)
            }
        }
    }

    override suspend fun getEvents(from: Long?, to: Long?): RealmResults<HolidayDTO> {
        return realm.where<HolidayDTO>().between("date", from!!, to!!).findAllAsync()
    }

    override fun fetchAllLocalData(): RealmResults<HolidayDTO> {
        return realm.where<HolidayDTO>().findAllAsync()
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

