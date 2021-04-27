package com.beyondinc.commandcenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.databinding.BriefBinding
import com.beyondinc.commandcenter.viewmodel.BriefViewModel

class RecyclerAdapterPopup(private val viewModel: BriefViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = BriefBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class ItemViewHolder(private val binding: BriefBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: BriefViewModel?, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}