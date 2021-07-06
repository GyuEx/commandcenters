package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentAgencyDetailListBinding
import com.beyondinc.commandcenter.databinding.FragmentAgencyDetailListTempBinding
import com.beyondinc.commandcenter.databinding.FragmentOrderDetailBinding
import com.beyondinc.commandcenter.databinding.FragmentOrderDetailListBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.AgencyViewModel
import com.beyondinc.commandcenter.viewmodel.MainsViewModel

class AgencyFragment : Fragment() {

    var binding: FragmentAgencyDetailListTempBinding? = null
    var viewModel : AgencyViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agency_detail_list_temp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        if(Vars.AgencyVm == null) Vars.AgencyVm = AgencyViewModel()
        binding!!.viewModel = Vars.AgencyVm
        binding!!.lifecycleOwner = requireActivity()
    }
}