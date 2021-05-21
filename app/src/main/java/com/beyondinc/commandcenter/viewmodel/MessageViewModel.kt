package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars

class MessageViewModel : ViewModel() {

    var msg : MutableLiveData<String> = MutableLiveData()
    var num = ""

    init {
        //Log.e("Message","viewModel Init")
    }

    fun click_ok(){
        if(msg.value == "배정") Vars.MainsHandler!!.obtainMessage(Finals.SUCCESS_MESSAGE,msg.value).sendToTarget()
        else if(msg.value == "배정취소") Vars.MainsHandler!!.obtainMessage(Finals.SUCCESS_MESSAGE,msg.value).sendToTarget()
        else if(msg.value == "오더완료") Vars.MainsHandler!!.obtainMessage(Finals.SUCCESS_MESSAGE,msg.value).sendToTarget()
        else if(msg.value == "오더취소") Vars.MainsHandler!!.obtainMessage(Finals.SUCCESS_MESSAGE,msg.value).sendToTarget()
        else if(msg.value == "포장상태변경") Vars.MainsHandler!!.obtainMessage(Finals.SUCCESS_MESSAGE,msg.value).sendToTarget()
        else if(msg.value == "가맹점에 전화") Vars.MainsHandler!!.obtainMessage(Finals.SEND_TELEPHONE,num).sendToTarget()
        else if(msg.value == "고객에게 전화") Vars.MainsHandler!!.obtainMessage(Finals.SEND_TELEPHONE,num).sendToTarget()
        else if(msg.value == "라이더에게 전화") Vars.MainsHandler!!.obtainMessage(Finals.SEND_TELEPHONE,num).sendToTarget()
        else if(msg.value == "고객주소변경") Vars.MainsHandler!!.obtainMessage(Finals.CHANGE_ADDRESS).sendToTarget()
        else if(msg.value!!.indexOf("라이더에게 배정") > 0) Vars.MapHandler!!.obtainMessage(Finals.ORDER_ASSIGN).sendToTarget() // 라이더명은 N이므로 indexOf로 처리

    }

    fun click_cancel(){
        Vars.MainsHandler!!.obtainMessage(Finals.CANCEL_MESSAGE).sendToTarget()
    }
}