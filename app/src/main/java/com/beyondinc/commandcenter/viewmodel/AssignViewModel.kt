package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterAssign
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

class AssignViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int,Orderdata>? = null // 리스트에 보여줄 아이템 목록
    var adapter: RecyclerAdapterAssign? = null // 리스트 어뎁터
    var sendedItem : Orderdata? = null // 선택된 오더 저장 클래스

    init{
        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapterAssign(this)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Vars.AssignVm = null
    }

    fun maincloseDetail(){
        sendedItem = null
    }

    fun click_list(pos: Int){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.MAP_FOR_SOPEN,0,items!![pos]!!).sendToTarget()
        sendedItem = items!![pos]!!
    }

    fun insertLogic(obj: ConcurrentHashMap<Int,Orderdata>) {
        items!!.clear()
        items!!.putAll(obj)
        create()
    }

    fun clearLogic(){
        items!!.clear()
        create()
    }

    fun create() {

        if(sendedItem != null)
        {
            sendedItem = Vars.orderList[sendedItem!!.OrderId]
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEND_ITEM,0,sendedItem).sendToTarget()
        }
        adapter!!.notifyDataSetChanged()
    }

    fun getTitle(pos: Int): String? {
        if(items!!.keys.size > 0) return "${items!![pos]!!.AgencyName}  >  ${items!![pos]!!.CustomerLongAddr}"
        else return "-"
    }

    fun getState(pos: Int): String? {
        if(items!!.keys.size > 0) return items!![pos]!!.DeliveryStateName
        else return "-"
    }

}