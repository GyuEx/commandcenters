package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars

class MessageViewModel : ViewModel() {

    var msg : MutableLiveData<String> = MutableLiveData() // 메세지
    var num = "" // 전화걸기시 전화번호 넘어올 변수

    init {
        //Log.e("Message","viewModel Init")
    }

    fun click_ok(){
        if(msg.value == "배정") Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SUCCESS_MESSAGE,0,msg.value).sendToTarget()
        else if(msg.value == "배정취소") Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SUCCESS_MESSAGE,0,msg.value).sendToTarget()
        else if(msg.value == "오더완료") Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SUCCESS_MESSAGE,0,msg.value).sendToTarget()
        else if(msg.value == "오더취소") Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SUCCESS_MESSAGE,0,msg.value).sendToTarget()
        else if(msg.value == "포장상태변경") Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SUCCESS_MESSAGE,0,msg.value).sendToTarget()
        else if(msg.value == "가맹점에 전화") Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEND_TELEPHONE,0,num).sendToTarget()
        else if(msg.value == "고객에게 전화") Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEND_TELEPHONE,0,num).sendToTarget()
        else if(msg.value == "라이더에게 전화") Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEND_TELEPHONE,0,num).sendToTarget()
        else if(msg.value == "고객주소변경") Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CHANGE_ADDRESS,0).sendToTarget()
        else if(msg.value!!.indexOf("라이더에게 배정") > 0) Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.ORDER_ASSIGN,0).sendToTarget() // 라이더명은 N이므로 indexOf로 처리

    }

    fun click_cancel(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CANCEL_MESSAGE,0).sendToTarget()
    }
}