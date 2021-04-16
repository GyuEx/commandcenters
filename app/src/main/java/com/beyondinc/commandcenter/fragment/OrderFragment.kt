package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {
    var binding: FragmentOrderBinding? = null
    var viewModel: MainsViewModel? = null

    companion object {
        var fr: Fragment? = null
        var listfrag: Fragment? = null
        var fragmentTransaction: FragmentTransaction? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MainsViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()

        fragmentTransaction = childFragmentManager.beginTransaction()
        listfrag = ListFragment()
        fr = listfrag
        fragmentTransaction!!.add(R.id.ofL02, fr!!)
        fragmentTransaction!!.commitAllowingStateLoss()
    }
}