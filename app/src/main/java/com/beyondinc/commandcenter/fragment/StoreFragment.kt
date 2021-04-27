package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentPopupListStoreBinding
import com.beyondinc.commandcenter.viewmodel.StoreViewModel

class StoreFragment : Fragment(){
    private var binding: FragmentPopupListStoreBinding? = null
    private var viewModel: StoreViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popup_list_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = StoreViewModel()
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()
    }
}