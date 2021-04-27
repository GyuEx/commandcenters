package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beyondinc.commandcenter.viewmodel.ItemViewModel
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentListBinding
import com.beyondinc.commandcenter.handler.MainThread
import com.beyondinc.commandcenter.util.Vars

class ListFragment : Fragment() {
    private var binding: FragmentListBinding? = null
    private var viewModel: ItemViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = ItemViewModel()
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}