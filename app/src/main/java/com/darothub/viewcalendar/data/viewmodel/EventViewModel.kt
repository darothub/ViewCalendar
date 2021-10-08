package com.darothub.viewcalendar.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.darothub.viewcalendar.data.repository.EventRepository
import com.darothub.viewcalendar.data.repository.remote.EventRemoteDataSource
import com.darothub.viewcalendar.model.EventRequest
import com.darothub.viewcalendar.model.Holiday
import com.darothub.viewcalendar.model.HolidayDTO
import com.darothub.viewcalendar.model.UIState
import com.darothub.viewcalendar.services.local.EventRealmDao
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.RealmList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
):ViewModel() {
    private val TAG by lazy { this::class.qualifiedName!! }
    private val _uiState = MutableStateFlow<UIState>(UIState.Nothing)
    val uiState: StateFlow<UIState> get() = _uiState
    var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun getEvent(eventRequest: EventRequest?=null) {
        _uiState.value = UIState.Loading
        try {
            if (eventRequest != null){
                val g = eventRepository.getCachedEvent(eventRequest)
                _uiState.value = UIState.Success(g)
                Log.i("Remote", g.toString())
            }
            else{
                val g = eventRepository.getCachedEvent()
                if (g.isNotEmpty()) _uiState.value = UIState.Success(g)
                else _uiState.value = UIState.Nothing
                Log.i("LocalViewmodel", g.toString())
            }
        }
        catch (e:Throwable){
            _uiState.value = UIState.Error(e)

        }
    }

}