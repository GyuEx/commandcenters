package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentOrderDetailBinding
import com.beyondinc.commandcenter.databinding.FragmentOrderDetailListBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel

class DetailFragment : Fragment() {

    var binding: FragmentOrderDetailListBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_detail_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        if(Vars.MainVm == null) Vars.MainVm = MainsViewModel()
        binding!!.viewModel = Vars.MainVm
        binding!!.lifecycleOwner = requireActivity()
    }
}