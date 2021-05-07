package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.NickPopupBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import com.beyondinc.commandcenter.viewmodel.SettingViewModel

class NickDialog : DialogFragment() {

    var binding: NickPopupBinding? = null
    var viewModel: SettingViewModel? = null

    companion object {
        var fr: Fragment? = null
        var nickFragment: Fragment? = null
        var fragmentTransaction: FragmentTransaction? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.nick_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()

        fragmentTransaction = childFragmentManager.beginTransaction()
        nickFragment = NickListFragment()
        fr = nickFragment
        fragmentTransaction!!.replace(R.id.snL01, fr!!)
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