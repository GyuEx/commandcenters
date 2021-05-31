package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction

import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentMapBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MapViewModel
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment

class MapFragment : Fragment() , OnMapReadyCallback {
    var binding: FragmentMapBinding? = null

    companion object {
        var mapFragment: MapFragment? = null
        var sublistfragment : SubListFragment? = null
        var subriderlistfragment : SubRiderListFragment? = null
        var assignfragment : AssignFragment? = null
        var fragmentTransaction: FragmentTransaction? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.e("MapFragment", "onCreate")
        binding = DataBindingUtil.bind(view)
        if(Vars.MapVm == null) Vars.MapVm = MapViewModel()
        binding!!.viewModel = Vars.MapVm
        binding!!.lifecycleOwner = requireActivity()

        NaverMapSdk.getInstance(requireActivity()).client = NaverMapSdk.NaverCloudPlatformClient("lb5lxom6no")

        fragmentTransaction = childFragmentManager.beginTransaction()
        mapFragment = MapFragment()
        fragmentTransaction!!.add(R.id.mL01, mapFragment!!)
        fragmentTransaction!!.show(mapFragment!!)
        fragmentTransaction!!.commitAllowingStateLoss()

        mapFragment!!.getMapAsync(this)

        fragmentTransaction = childFragmentManager.beginTransaction()
        sublistfragment = SubListFragment()
        fragmentTransaction!!.add(R.id.subL01, sublistfragment!!)
        fragmentTransaction!!.show(sublistfragment!!)
        fragmentTransaction!!.commitAllowingStateLoss()

        fragmentTransaction = childFragmentManager.beginTransaction()
        subriderlistfragment = SubRiderListFragment()
        fragmentTransaction!!.add(R.id.subR01, subriderlistfragment!!)
        fragmentTransaction!!.show(subriderlistfragment!!)
        fragmentTransaction!!.commitAllowingStateLoss()

        fragmentTransaction = childFragmentManager.beginTransaction()
        assignfragment = AssignFragment()
        fragmentTransaction!!.add(R.id.mvL01, assignfragment!!)
        fragmentTransaction!!.show(assignfragment!!)
        fragmentTransaction!!.commitAllowingStateLoss()

    }

    override fun onMapReady(p0: NaverMap) {
        //Log.e("MAP","On Map Ready?")
        Vars.MapVm?.mapInstance = p0
        Vars.MapVm?.Mapclick()
    }
}