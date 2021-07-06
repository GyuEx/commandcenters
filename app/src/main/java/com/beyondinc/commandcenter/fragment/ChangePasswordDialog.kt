package com.beyondinc.commandcenter.fragment

import android.content.Context
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
import com.beyondinc.commandcenter.databinding.SelectDialogBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.ChangePasswordViewModel
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import com.beyondinc.commandcenter.viewmodel.SettingViewModel

class ChangePasswordDialog(pw:String,txt:String) : DialogFragment(){

    var binding: ChangePasswordDialogBinding? = null
    var viewModel : ChangePasswordViewModel? = null
    var title = txt
    var pw = pw

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.change_password_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = ChangePasswordViewModel()
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = this // 오너는 단독으로 움직일 너가 가져가도록

        viewModel!!.titletext.postValue(title)
        viewModel!!.retropassword.postValue(pw)
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = Vars.DeviceSize.x
        val deviceHeight = Vars.DeviceSize.y
        params?.width = (deviceWidth).toInt()
        params?.height = (deviceHeight).toInt() / 2
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    fun exit()
    {
        dismiss()
    }
}
