package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentPopupListNickBinding
import com.beyondinc.commandcenter.viewmodel.NickViewModel

class NickListFragment : Fragment() {
    private var binding: FragmentPopupListNickBinding? = null
    private var viewModel: NickViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popup_list_nick, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = NickViewModel()
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