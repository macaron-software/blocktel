package com.macaron.blocktel.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macaron.blocktel.BlockedCallLog
import com.macaron.blocktel.databinding.ItemRecentCallBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecentCallsAdapter(
    private var entries: List<BlockedCallLog.Entry>
) : RecyclerView.Adapter<RecentCallsAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.FRANCE)

    inner class ViewHolder(val binding: ItemRecentCallBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentCallBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.binding.apply {
            textNumber.text = formatNumber(entry.number)
            textPrefix.text = "Prefixe ${entry.prefix}"
            textTime.text = dateFormat.format(Date(entry.timestamp))
        }
    }

    override fun getItemCount(): Int = entries.size

    fun update(newEntries: List<BlockedCallLog.Entry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    private fun formatNumber(number: String): String {
        if (number.length != 10) return number
        return "${number.substring(0, 2)} ${number.substring(2, 4)} ${number.substring(4, 6)} ${number.substring(6, 8)} ${number.substring(8, 10)}"
    }
}
