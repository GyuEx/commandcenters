package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.*
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.AgencyViewModel
import com.beyondinc.commandcenter.viewmodel.RiderListViewModel

class RiderListFragment : Fragment() {

    var binding: FragmentListRiderlistBinding? = null
    var viewModel : AgencyViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_riderlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        if(Vars.RiderListVm == null) Vars.RiderListVm = RiderListViewModel()
        binding!!.viewModel = Vars.RiderListVm
        binding!!.lifecycleOwner = requireActivity()
    }
}