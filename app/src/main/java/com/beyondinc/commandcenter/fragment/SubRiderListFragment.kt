package com.beyondinc.commandcenter.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beyondinc.commandcenter.viewmodel.ItemViewModel
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentListBinding
import com.beyondinc.commandcenter.databinding.FragmentListSubBinding
import com.beyondinc.commandcenter.databinding.FragmentListSubRiderBinding
import com.beyondinc.commandcenter.handler.MainThread
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.SubItemViewModel
import com.beyondinc.commandcenter.viewmodel.SubRiderViewModel

class SubRiderListFragment : Fragment() {
    private var binding: FragmentListSubRiderBinding? = null
    private var viewModel: SubRiderViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_sub_rider, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = SubRiderViewModel()
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