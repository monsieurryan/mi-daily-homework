package com.example.okhttpquerydemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.okhttpquerydemo.databinding.ItemHolidayBinding

class HolidayAdapter : RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder>() {
    private var holidays: List<Holiday> = emptyList()

    fun submitList(newHolidays: List<Holiday>) {
        holidays = newHolidays
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder {
        val binding = ItemHolidayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HolidayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        holder.bind(holidays[position])
    }

    override fun getItemCount() = holidays.size

    class HolidayViewHolder(
        private val binding: ItemHolidayBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(holiday: Holiday) {
            binding.dateText.text = holiday.date
            binding.localNameText.text = holiday.localName
            binding.nameText.text = holiday.name
        }
    }
} 