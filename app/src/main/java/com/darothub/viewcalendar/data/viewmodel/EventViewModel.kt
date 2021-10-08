package com.darothub.viewcalendar.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.darothub.viewcalendar.data.repository.EventRepository
import com.darothub.viewcalendar.data.repository.remote.EventRemoteDataSource
import com.darothub.viewcalendar.model.*
import com.darothub.viewcalendar.services.local.EventRealmDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.RealmList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import retrofit2.HttpException
import java.io.Reader
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
):ViewModel() {
    private val TAG by lazy { this::class.qualifiedName!! }
    private val _uiState = MutableStateFlow<UIState>(UIState.Nothing)
    val uiState: StateFlow<UIState> get() = _uiState

    suspend fun getEvent(eventRequest: EventRequest?=null) {
        _uiState.value = UIState.Loading
        try {
            if (eventRequest != null){
                val res = eventRepository.getCachedEvent(eventRequest)
                when {
                    res.error -> {
                        _uiState.value = UIState.Error(res.reason ?: "Error")
                    }
                    res.holidayDTOs.isNotEmpty() -> {
                        _uiState.value = UIState.Success(res.holidayDTOs)
                    }
                    else -> {
                        _uiState.value = UIState.Nothing
                    }
                }
            }
            else{
                val res = eventRepository.getCachedEvent()
                if (res.holidayDTOs.isNotEmpty()) _uiState.value = UIState.Success(res.holidayDTOs)
                else _uiState.value = UIState.Nothing
            }
        }
        catch (e:HttpException){
            val msg = errorConverter(e.response()?.errorBody()?.charStream())
            _uiState.value = UIState.Error(msg)
        }
        catch (e:Exception){
            _uiState.value = UIState.Error("Bad connection")
        }

    }

    private fun errorConverter(
        errorBody: Reader?
    ): String {
        val gson = Gson()
        val type = object : TypeToken<RemoteResponse>() {}.type
        val errorResponse: RemoteResponse? = gson.fromJson(errorBody, type)

        return errorResponse?.reason.toString()
    }

}