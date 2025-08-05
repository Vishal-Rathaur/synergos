package com.synergos.partner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.synergos.partner.databinding.ItemHealthDataBinding
import com.synergos.partner.model.HealthData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HealthDataAdapter(
    private val onEdit: (HealthData) -> Unit,
    private val onDelete: (HealthData) -> Unit
) : ListAdapter<HealthData, HealthDataAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HealthData>() {
            override fun areItemsTheSame(old: HealthData, new: HealthData) = old.id == new.id
            override fun areContentsTheSame(old: HealthData, new: HealthData) = old == new
        }
    }

    inner class ViewHolder(val binding: ItemHealthDataBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHealthDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvMetric.text = "${item.type}: ${item.value}"
        holder.binding.tvTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(item.timestamp))

        holder.binding.btnEdit.setOnClickListener {
            onEdit(item)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDelete(item)
        }

    }
}
