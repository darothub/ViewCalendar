package com.darothub.viewcalendar.data.viewmodel

import androidx.lifecycle.ViewModel
import com.darothub.viewcalendar.data.repository.EventRepository
import com.darothub.viewcalendar.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import java.io.Reader
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
):ViewModel() {
    private val TAG by lazy { this::class.qualifiedName!! }
    private val _uiState = MutableStateFlow<UIState>(UIState.Nothing)
    val uiState: StateFlow<UIState> get() = _uiState

    suspend fun getEvents(eventRequest: EventRequest?=null) {
        _uiState.value = UIState.Loading
        try {
            if (eventRequest != null){
                val res = eventRepository.getRemoteEvent(eventRequest)
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
        val type = object : TypeToken<DomainEvent>() {}.type
        val errorResponse: DomainEvent? = gson.fromJson(errorBody, type)

        return errorResponse?.reason.toString()
    }

}