package com.beyondinc.commandcenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.databinding.AssignBinding
import com.beyondinc.commandcenter.databinding.ItemBinding
import com.beyondinc.commandcenter.databinding.SubitemBinding
import com.beyondinc.commandcenter.viewmodel.AssignViewModel
import com.beyondinc.commandcenter.viewmodel.ItemViewModel
import com.beyondinc.commandcenter.viewmodel.SubItemViewModel

class RecyclerAdapterAssign(private val viewModel: AssignViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = AssignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class ItemViewHolder(private val binding: AssignBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: AssignViewModel?, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}