package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentListCheckBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.CheckViewModel
import com.beyondinc.commandcenter.viewmodel.MainsViewModel

class CheckListFragment : Fragment() {
    private var binding: FragmentListCheckBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        if(Vars.CheckVm == null) Vars.CheckVm = CheckViewModel()
        binding!!.viewModel = Vars.CheckVm
        binding!!.lifecycleOwner = requireActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Check","Destroy")
        Vars.CheckVm = null
    }
}