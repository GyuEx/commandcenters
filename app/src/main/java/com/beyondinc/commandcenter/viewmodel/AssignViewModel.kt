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

    init{
        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(HashMap<Int,Orderdata>()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapterAssign(this)
        }

        Vars.AssignHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("SubriderHandler",msg.what.toString())
                if (msg.what == Finals.INSERT_ORDER) insertLogic(msg.obj as HashMap<Int,Orderdata>)
            }
        }
    }

    fun insertLogic(obj: HashMap<Int,Orderdata>) {
        items!!.clear()
        items!!.putAll(obj)
        create()
    }

    fun create() {
        adapter!!.notifyDataSetChanged()
    }

    fun getTitle(pos: Int): String? {
        return "${items!![pos]!!.AgencyName}  >  ${items!![pos]!!.CustomerLongAddr}"
    }

    fun getState(pos: Int): String? {
        return items!![pos]!!.DeliveryStateName
    }

}