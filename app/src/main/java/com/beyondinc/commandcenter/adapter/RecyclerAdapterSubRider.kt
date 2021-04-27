package com.beyondinc.commandcenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.databinding.ItemBinding
import com.beyondinc.commandcenter.databinding.SubitemBinding
import com.beyondinc.commandcenter.databinding.SubriderBinding
import com.beyondinc.commandcenter.viewmodel.ItemViewModel
import com.beyondinc.commandcenter.viewmodel.SubItemViewModel
import com.beyondinc.commandcenter.viewmodel.SubRiderViewModel

class RecyclerAdapterSubRider(private val viewModel: SubRiderViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = SubriderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class ItemViewHolder(private val binding: SubriderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: SubRiderViewModel?, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}