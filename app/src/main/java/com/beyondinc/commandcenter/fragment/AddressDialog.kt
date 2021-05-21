package com.beyondinc.commandcenter.fragment

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.AddressDialogBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.AddressViewModel
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback


class AddressDialog : DialogFragment(), OnMapReadyCallback {

    var binding: AddressDialogBinding? = null
    var viewModel: AddressViewModel? = null
    var mapfrag: MapFragment? = null
    var fragmentTransaction: FragmentTransaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.address_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(AddressViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()

        fragmentTransaction = childFragmentManager.beginTransaction()
        mapfrag = MapFragment()
        fragmentTransaction!!.add(R.id.adML01, mapfrag!!)
        fragmentTransaction!!.commitAllowingStateLoss()
        mapfrag!!.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = Vars.DeviceSize.x
        val deviceHeight = Vars.DeviceSize.y
        params?.width = (deviceWidth).toInt()
        params?.height = (deviceHeight).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onMapReady(p0: NaverMap) {
        Log.e("On Map","Ready?")
        viewModel?.mapInstance = p0
        viewModel?.makeMarker()
    }
}
