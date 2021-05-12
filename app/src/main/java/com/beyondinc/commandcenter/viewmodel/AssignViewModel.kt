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
    var items: ConcurrentHashMap<Int,Orderdata>? = null
    var adapter: RecyclerAdapterAssign? = null
    var sendedItem : Orderdata? = null

    init{
        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapterAssign(this)
        }

        Vars.AssignHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("SubriderHandler",msg.what.toString())
                if (msg.what == Finals.INSERT_ORDER) insertLogic(msg.obj as ConcurrentHashMap<Int,Orderdata>)
                else if(msg.what == Finals.SELECT_EMPTY) clearLogic()
                else if(msg.what == Finals.ORDER_DETAIL_CLOSE) maincloseDetail()
            }
        }
    }

    fun maincloseDetail(){
        sendedItem = null
    }

    fun click_list(pos: Int){
        Vars.MainsHandler!!.obtainMessage(Finals.MAP_FOR_SOPEN,items!![pos]!!).sendToTarget()
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
            Vars.MainsHandler!!.obtainMessage(Finals.SEND_ITEM,sendedItem).sendToTarget()
        }

        adapter!!.notifyDataSetChanged()
    }

    fun getTitle(pos: Int): String? {
        if(items!!.keys.size > 0) return "${items!![pos]!!.AgencyName}  >  ${items!![pos]!!.CustomerLongAddr}"
        else return ""
    }

    fun getState(pos: Int): String? {
        if(items!!.keys.size > 0) return items!![pos]!!.DeliveryStateName
        else return ""
    }

}