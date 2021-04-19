package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentMapBinding
import com.beyondinc.commandcenter.databinding.FragmentOrderBinding
import com.beyondinc.commandcenter.viewmodel.MainsViewModel

class MapFragment : Fragment() {
    var binding: FragmentMapBinding? = null
    var viewModel: MainsViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MainsViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()
    }
}