package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentPopupListBriefBinding
import com.beyondinc.commandcenter.viewmodel.BriefViewModel

class BriefFragment : Fragment(){
    private var binding: FragmentPopupListBriefBinding? = null
    private var viewModel: BriefViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popup_list_brief, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = BriefViewModel()
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()
    }
}