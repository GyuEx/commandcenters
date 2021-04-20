package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction

import com.naver.maps.map.MapFragment

import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.activity.Mains
import com.beyondinc.commandcenter.databinding.FragmentMapBinding
import com.beyondinc.commandcenter.databinding.FragmentOrderBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import com.naver.maps.map.NaverMapSdk

class DriverFragment : Fragment() {
    var binding: FragmentMapBinding? = null
    var viewModel: MainsViewModel? = null

    companion object {
        var fr: Fragment? = null
        var mapFragment: MapFragment? = null
        var fragmentTransaction: FragmentTransaction? = null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MainsViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()

        NaverMapSdk.getInstance(requireContext()).client = NaverMapSdk.NaverCloudPlatformClient("lb5lxom6no")

        fragmentTransaction = childFragmentManager.beginTransaction()
        mapFragment = MapFragment()
        fr = mapFragment
        fragmentTransaction!!.replace(R.id.dfL01, fr!!)
        fragmentTransaction!!.show(fr!!)
        fragmentTransaction!!.commitAllowingStateLoss()
    }
}