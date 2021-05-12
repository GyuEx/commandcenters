package com.beyondinc.commandcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.databinding.SubitemBinding
import com.beyondinc.commandcenter.viewmodel.SubItemViewModel

class RecyclerAdapterSub(private val viewModel: SubItemViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = SubitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(viewModel, position)
        }
    }

    override fun getItemCount(): Int {
        return if (viewModel.itemss == null) 0 else viewModel?.itemss!!.size
    }

    class ItemViewHolder(private val binding: SubitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: SubItemViewModel?, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}