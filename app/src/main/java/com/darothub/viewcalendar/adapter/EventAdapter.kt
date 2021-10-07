package com.darothub.viewcalendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darothub.viewcalendar.databinding.EventItemViewBinding
import com.darothub.viewcalendar.model.Holiday
import com.darothub.viewcalendar.utils.getColorCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventAdapter : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    lateinit var date:String
    val dataList = mutableListOf<Holiday>()
    private val formatter = DateTimeFormatter.ofPattern("EEE'\n'dd MMM")
    inner class EventViewHolder(private val binding: EventItemViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(event:Holiday){
            binding.itemEventDateText.apply {
                val local = LocalDate.parse(date)
                text = formatter.format(local.atStartOfDay())
                setBackgroundColor(itemView.context.getColorCompat(event.color))
            }
            binding.itemEventType.text = event.type
            binding.itemEventName.text = event.name
        }
    }

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