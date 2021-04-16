package com.beyondinc.commandcenter.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.activity.Mains
import com.beyondinc.commandcenter.databinding.ActivityMainBinding
import com.beyondinc.commandcenter.databinding.DetailsPopupBinding
import com.beyondinc.commandcenter.databinding.DialogBinding
import com.beyondinc.commandcenter.databinding.DialogPopupBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.DialogViewModel
import com.beyondinc.commandcenter.viewmodel.MainsViewModel

class HistoryDialog : DialogFragment() {

    var binding: DetailsPopupBinding? = null
    var viewModel: MainsViewModel? = null

    companion object {
        var fr: Fragment? = null
        var historyFragment: Fragment? = null
        var fragmentTransaction: FragmentTransaction? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.details_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MainsViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()

        fragmentTransaction = childFragmentManager.beginTransaction()
        historyFragment = HistoryFragment()
        fr = historyFragment
        fragmentTransaction!!.add(R.id.dpL01, fr!!)
        fragmentTransaction!!.commitAllowingStateLoss()
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = Vars.DeviceSize.x
        params?.width = (deviceWidth).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}