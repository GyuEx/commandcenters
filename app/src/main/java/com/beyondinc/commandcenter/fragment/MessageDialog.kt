package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.MessageDialogBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MessageViewModel

class MessageDialog(i:String) : DialogFragment(){

    var binding: MessageDialogBinding? = null
    var viewModel: MessageViewModel? = null
    var num = i

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.message_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MessageViewModel::class.java)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()
        viewModel!!.msg.postValue(tag)
        viewModel!!.num = num // 전화걸기 기능 전화번호 전달
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = Vars.DeviceSize.x
        val deviceHeight = Vars.DeviceSize.y
        params?.width = (deviceWidth).toInt()
        params?.height = (deviceHeight).toInt()/4
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}
