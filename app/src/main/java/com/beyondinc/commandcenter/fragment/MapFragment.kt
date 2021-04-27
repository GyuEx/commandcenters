package com.beyondinc.commandcenter.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction

import com.naver.maps.map.MapFragment

import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.Interface.MapFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentMapBinding
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

class MapFragment : Fragment() , MapFun{
    var binding: FragmentMapBinding? = null
    var viewModel: MainsViewModel? = null

    companion object {
        var fr: Fragment? = null
        var mapFragment: MapFragment? = null
        var fragmentTransaction: FragmentTransaction? = null
        lateinit var mapInstance: NaverMap
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("MapFragment", "onCreate")
        binding = DataBindingUtil.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MainsViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()

        NaverMapSdk.getInstance(requireContext()).client = NaverMapSdk.NaverCloudPlatformClient("lb5lxom6no")

        fragmentTransaction = childFragmentManager.beginTransaction()
        mapFragment = MapFragment()
        fr = mapFragment
        fragmentTransaction!!.add(R.id.dfL01, fr!!)
        fragmentTransaction!!.show(fr!!)
        fragmentTransaction!!.commitAllowingStateLoss()
    }

    override fun createRider() {
        var it : Iterator<String> = Vars.riderList.keys.iterator()
        while (it.hasNext())
        {
            var itk = it.next()
            var rit : Iterator<String> = Vars.riderList[itk]!!.keys.iterator()
            while (rit.hasNext())
            {
                Log.e("Aaaaaaaaaa"," " + Vars.riderList[itk]!![rit])
                val ritd = rit.next()
                if(Vars.riderList[itk]!![ritd]!!.workingStateCode == Codes.RIDER_OFF_WORK)
                {
                    continue
                }
                val latitude = Vars.riderList[itk]!![ritd]?.latitude?.toDouble()
                val longitude = Vars.riderList[itk]!![ritd]?.longitude?.toDouble()
                if (latitude != null && longitude != null) {
                    val marker = Marker()
                    val position = LatLng(latitude, longitude)
                    marker.icon = OverlayImage.fromView(
                        RiderMarketView(
                            Vars.mContext!!,
                            Vars.riderList[itk]!![ritd]!!.name!!,
                            Vars.riderList[itk]!![ritd]!!.assignCount!!.toInt(),
                            Vars.riderList[itk]!![ritd]!!.pickupCount!!.toInt()
                        )
                    )
                    marker.position = position
                    marker.setOnClickListener {
                        mapInstance.moveCamera(CameraUpdate.scrollTo(marker.position))
                        return@setOnClickListener false
                    }
                    marker.map = mapInstance
                }
            }
        }
    }

    class RiderMarketView(
        context: Context,
        riderName: String,
        riderAssignCount: Int,
        riderPickupCount: Int
    ) : ConstraintLayout(context) {

        init {
            val view: View = View.inflate(context, R.layout.view_rider_marker, this)
            val backgroundResourceID: Int =
                if (riderAssignCount == 0 && riderPickupCount == 0)
                    R.drawable.ic_marker_idle_rider
                else
                    R.drawable.ic_marker_assigned_rider
            view.setBackgroundResource(backgroundResourceID)
            view.background.alpha = 196

            val nameField: TextView = view.findViewById(R.id.textRiderName)
            val statusField: TextView = view.findViewById(R.id.textRiderStatus)

            nameField.text = riderName
            statusField.text =
                String.format(
                    context.resources.getString(R.string.format_text_rider_status),
                    riderAssignCount, riderPickupCount
                )
        }
    }
}