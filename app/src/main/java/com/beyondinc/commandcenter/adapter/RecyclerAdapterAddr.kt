package com.beyondinc.commandcenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.databinding.AddrBinding
import com.beyondinc.commandcenter.databinding.ItemBinding
import com.beyondinc.commandcenter.viewmodel.AddressViewModel
import com.beyondinc.commandcenter.viewmodel.ItemViewModel

class RecyclerAdapterAddr(private val viewModel: AddressViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = AddrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(viewModel, position)
        }
    }

    override fun getItemCount(): Int {
        return if (viewModel.itemsAddr == null) 0 else viewModel?.itemsAddr!!.size
    }

    class ItemViewHolder(private val binding: AddrBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: AddressViewModel?, pos: Int) {
            binding.viewModel = viewModel
            binding.pos = pos
            binding.executePendingBindings()
        }
    }
}