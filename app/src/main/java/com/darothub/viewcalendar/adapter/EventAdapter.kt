package com.darothub.viewcalendar.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darothub.viewcalendar.databinding.EventItemViewBinding
import com.darothub.viewcalendar.model.DomainEvent
import com.darothub.viewcalendar.model.DomainEvents
import com.darothub.viewcalendar.utils.getColorCompat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventAdapter : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    lateinit var date:String
    private lateinit var domainEvents:DomainEvents
    val dataList = mutableListOf<DomainEvent>()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    inner class EventViewHolder(private val binding: EventItemViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(event:DomainEvent){
            binding.itemEventDateText.apply {
                val local = LocalDate.parse(date)
                Log.d("Adapter", local.toString())
                text = formatter.format(local.atStartOfDay())
                setBackgroundColor(itemView.context.getColorCompat(event.color))
            }
            binding.itemEventType.text = event.type
            binding.itemEventName.text = event.name
        }
    }
//    @JvmName("setDomainEvents1")
//    fun setDomainEvents(domainEvents: DomainEvents){
//        this.domainEvents = domainEvents
//    }
//    fun setDomainEvent(){
//        val data = domainEvents.holidays[date]
//        if (data != null) {
//            dataList.addAll(data)
//            notifyDataSetChanged()
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            EventItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size
}