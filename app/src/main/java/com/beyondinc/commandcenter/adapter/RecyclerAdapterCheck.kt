package com.beyondinc.commandcenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.activity.Mains
import com.beyondinc.commandcenter.databinding.CheckBinding
import com.beyondinc.commandcenter.viewmodel.CheckViewModel
import com.beyondinc.commandcenter.viewmodel.MainsViewModel

class RecyclerAdapterCheck(private val viewModel: CheckViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(viewModel, position)
        }
    }

    override fun getItemCount(): Int {
        return if (viewModel.items == null) 0 else viewModel?.items!!.size
    }

    class ItemViewHolder(private val binding: CheckBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: CheckViewModel?, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}