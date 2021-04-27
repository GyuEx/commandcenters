package com.beyondinc.commandcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.databinding.RiderBinding
import com.beyondinc.commandcenter.viewmodel.RiderViewModel

class RecyclerAdapterRider(private val viewModel: RiderViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = RiderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class ItemViewHolder(private val binding: RiderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: RiderViewModel, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}