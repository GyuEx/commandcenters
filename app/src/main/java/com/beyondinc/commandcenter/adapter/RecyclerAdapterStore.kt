package com.beyondinc.commandcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.databinding.StoreBinding
import com.beyondinc.commandcenter.viewmodel.StoreViewModel

class RecyclerAdapterStore(private val viewModel: StoreViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = StoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class ItemViewHolder(private val binding: StoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: StoreViewModel?, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}