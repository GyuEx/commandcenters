package com.beyondinc.commandcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.databinding.DialogBinding
import com.beyondinc.commandcenter.databinding.HistoryBinding
import com.beyondinc.commandcenter.viewmodel.DialogViewModel
import com.beyondinc.commandcenter.viewmodel.HistoryViewModel

class RecyclerAdapterHistory(private val viewModel: HistoryViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = HistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(viewModel, position)
        }
    }

    override fun getItemCount(): Int {
        return if (viewModel.getItems() == null) 0 else viewModel!!.getItems()!!.size
    }

    class ItemViewHolder(private val binding: HistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: HistoryViewModel?, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}