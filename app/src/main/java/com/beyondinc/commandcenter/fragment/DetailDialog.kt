package com.beyondinc.commandcenter.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentOrderDetailBinding
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class DetailDialog(i: Int) : DialogFragment() , OnMapReadyCallback {

    var binding: FragmentOrderDetailBinding? = null
    var popcode = i

    companion object {
        var orderfrag: Fragment? = null
        var agencyfrag: Fragment? = null
        var riderlistfrag : Fragment? = null
        var mapfrag: MapFragment? = null
        var fragmentTransaction: FragmentTransaction? = null
        var code = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        //isCancelable = false
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.e("Dialog","Dissmiss")
        Vars.MainVm?.closeDetail()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Vars.dContext = this.context

        binding = DataBindingUtil.bind(view)
        if(Vars.MainVm == null) Vars.MainVm = MainsViewModel()
        binding!!.viewModel = Vars.MainVm
        binding!!.lifecycleOwner = requireActivity()

        if(popcode == Finals.DETAIL_ORDER)
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            orderfrag = DetailFragment()
            fragmentTransaction!!.add(R.id.odL01, orderfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
        }
        else if(popcode == Finals.DETAIL_AGENCY)
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            agencyfrag = AgencyFragment()
            fragmentTransaction!!.add(R.id.odL01, agencyfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
        }
        else if(popcode == Finals.DETAIL_RIDER)
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            riderlistfrag = RiderDetailFragment()
            fragmentTransaction!!.add(R.id.odL01, riderlistfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
        }
        else
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            mapfrag = MapFragment()
            fragmentTransaction!!.add(R.id.odL01, mapfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
            mapfrag!!.getMapAsync(this)
        }
    }

    fun setFragment(i : Int) {
        popcode = i
        if(Vars.MainVm?.DetailsSelect!!.value == Finals.DETAIL_MAP)
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            mapfrag = MapFragment()
            fragmentTransaction!!.replace(R.id.odL01, mapfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
            mapfrag!!.getMapAsync(this)
        }
        else if(Vars.MainVm?.DetailsSelect!!.value == Finals.DETAIL_ORDER)
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            orderfrag = DetailFragment()
            fragmentTransaction!!.replace(R.id.odL01, orderfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
        }
        else if(Vars.MainVm?.DetailsSelect!!.value == Finals.DETAIL_AGENCY)
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            agencyfrag = AgencyFragment()
            fragmentTransaction!!.add(R.id.odL01, agencyfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
        }
        else if(Vars.MainVm?.DetailsSelect!!.value == Finals.DETAIL_RIDER)
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            riderlistfrag = RiderDetailFragment()
            fragmentTransaction!!.add(R.id.odL01, riderlistfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
        }
    }

    override fun onMapReady(p0: NaverMap) {
        if(Vars.MainVm?.mapInstance != null) Vars.MainVm?.mapInstance == null
        Vars.MainVm?.mapInstance = p0
        Vars.MainVm?.makeMarker(popcode)
    }
}