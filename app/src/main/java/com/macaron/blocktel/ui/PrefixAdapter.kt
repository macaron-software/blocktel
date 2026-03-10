package com.macaron.blocktel.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macaron.blocktel.PrefixRepository.PrefixRule
import com.macaron.blocktel.databinding.ItemPrefixBinding

class PrefixAdapter(
    private var rules: List<PrefixRule>,
    private val onToggle: (prefix: String, enabled: Boolean) -> Unit,
    private val onLongClick: (prefix: String) -> Unit
) : RecyclerView.Adapter<PrefixAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPrefixBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPrefixBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rule = rules[position]
        holder.binding.apply {
            textPrefix.text = rule.label
            textRegion.text = rule.region
            switchEnabled.isChecked = rule.enabled
            switchEnabled.setOnCheckedChangeListener { _, isChecked ->
                onToggle(rule.prefix, isChecked)
            }
            root.setOnLongClickListener {
                onLongClick(rule.prefix)
                true
            }
        }
    }

    override fun getItemCount(): Int = rules.size

    fun update(newRules: List<PrefixRule>) {
        rules = newRules
        notifyDataSetChanged()
    }
}
