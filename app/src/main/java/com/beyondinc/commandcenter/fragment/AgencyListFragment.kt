package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentListAgencyBinding
import com.beyondinc.commandcenter.databinding.FragmentListCheckBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.AgencyViewModel
import com.beyondinc.commandcenter.viewmodel.CheckViewModel
import com.beyondinc.commandcenter.viewmodel.MainsViewModel

class AgencyListFragment : Fragment() {
    private var binding: FragmentListAgencyBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_agency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        if(Vars.AgencyVm == null) Vars.AgencyVm = AgencyViewModel()
        binding!!.viewModel = Vars.AgencyVm
        binding!!.lifecycleOwner = requireActivity()
    }
}