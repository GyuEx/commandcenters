package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.ChangePasswordDialogBinding
import com.beyondinc.commandcenter.databinding.MsgDialogBinding
import com.beyondinc.commandcenter.databinding.SelectDialogBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.LoginViewModel
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import com.beyondinc.commandcenter.viewmodel.SettingViewModel

class MsgDialog : DialogFragment(){

    var binding: MsgDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.msg_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        if(Vars.LoginVm == null) Vars.LoginVm = LoginViewModel()
        binding!!.viewModel = Vars.LoginVm
        binding!!.lifecycleOwner = requireActivity()
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = Vars.DeviceSize.x
        val deviceHeight = Vars.DeviceSize.y
        params?.width = (deviceWidth).toInt()
        params?.height = (deviceHeight).toInt() / 3
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}
