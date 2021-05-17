package com.beyondinc.commandcenter.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.ActivityOderDetailBinding
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class DetailDialog : DialogFragment() , OnMapReadyCallback {

    var binding: ActivityOderDetailBinding? = null
    var viewModel: MainsViewModel? = null

    companion object {
        var oderfrag: Fragment? = null
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
        viewModel!!.closeDetail()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_oder_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Vars.dContext = this.context

        binding = DataBindingUtil.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MainsViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()

        fragmentTransaction = childFragmentManager.beginTransaction()
        oderfrag = DetailFragment()
        fragmentTransaction!!.add(R.id.odL01, oderfrag!!)
        fragmentTransaction!!.commitAllowingStateLoss()

//        oderfrag = DetailFragment()
//        mapfrag = MapFragment()
//
//        fragmentTransaction!!.add(R.id.odL01, oderfrag!!)
//        fragmentTransaction!!.add(R.id.odL01, mapfrag!!)
//        fragmentTransaction!!.hide(mapfrag!!)
//        fragmentTransaction!!.show(oderfrag!!)
//        fragmentTransaction!!.commit()
    }

    fun setFragment(i : Int) {
        code = i
        if(viewModel!!.DetailsSelect.value == Finals.DETAIL_MAP)
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            mapfrag = MapFragment()
            fragmentTransaction!!.replace(R.id.odL01, mapfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
            mapfrag!!.getMapAsync(this)
        }
        else if(viewModel!!.DetailsSelect.value == Finals.DETAIL_DETAIL)
        {
            fragmentTransaction = childFragmentManager.beginTransaction()
            oderfrag = DetailFragment()
            fragmentTransaction!!.replace(R.id.odL01, oderfrag!!)
            fragmentTransaction!!.commitAllowingStateLoss()
        }
    }

    override fun onMapReady(p0: NaverMap) {
        viewModel?.mapInstance = p0
        viewModel?.makeMarker(code)
    }
}