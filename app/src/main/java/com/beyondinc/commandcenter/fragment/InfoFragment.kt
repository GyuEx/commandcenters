package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.InfoFragmentBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import kotlinx.android.synthetic.main.activity_main.*

class InfoFragment : Fragment() {
    private var binding: InfoFragmentBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        if(Vars.MainVm == null) Vars.MainVm = MainsViewModel()
        binding!!.viewModel = Vars.MainVm
        binding!!.lifecycleOwner = requireActivity()
    }
}