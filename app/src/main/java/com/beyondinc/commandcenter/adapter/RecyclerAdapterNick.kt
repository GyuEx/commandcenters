package com.beyondinc.commandcenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.databinding.ItemBinding
import com.beyondinc.commandcenter.databinding.NickBinding
import com.beyondinc.commandcenter.databinding.SubitemBinding
import com.beyondinc.commandcenter.databinding.SubriderBinding
import com.beyondinc.commandcenter.viewmodel.ItemViewModel
import com.beyondinc.commandcenter.viewmodel.NickViewModel
import com.beyondinc.commandcenter.viewmodel.SubItemViewModel
import com.beyondinc.commandcenter.viewmodel.SubRiderViewModel

class RecyclerAdapterNick(private val viewModel: NickViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = NickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class ItemViewHolder(private val binding: NickBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: NickViewModel?, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}