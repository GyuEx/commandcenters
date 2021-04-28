package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterSub
import com.beyondinc.commandcenter.adapter.RecyclerAdapterSubRider
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.timer

class SubRiderViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int,Riderdata>? = null
    var adapter: RecyclerAdapterSubRider? = null

    var first: Boolean = false

    var select = MutableLiveData<Int>()

    init {
        Log.e("Memo", "Memo call")

        select.postValue(Finals.SELECT_EMPTY)

        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(HashMap<Int,Riderdata>()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapterSubRider(this)
        }

        Vars.SubRiderHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("MMMMMM",msg.what.toString())
                if (msg.what == Finals.INSERT_RIDER) insertLogic(msg.obj)
                else if(msg.what == Finals.SELECT_RIDER) select.postValue(Finals.SELECT_RIDER)
                else if(msg.what == Finals.SELECT_EMPTY) select.postValue(Finals.SELECT_EMPTY)
                else if(msg.what == Finals.MAP_FOR_CALL_RIDER) getRider(msg.obj)
            }
        }
    }

    fun getRider(obj:Any)
    {
        var Mid = obj
        var it : Iterator<Int> = items?.keys!!.iterator()
        while (it.hasNext())
        {
            var itt = it.next()
            if(items!![itt]!!.MakerID == Mid)
            {
                Vars.MapHandler!!.obtainMessage(Finals.MAP_MOVE_FOCUS, items!![itt]!!).sendToTarget()
                break
            }
        }
    }

    fun insertLogic(obj:Any)
    {
        items!!.clear()

        var list = obj as ConcurrentHashMap<String,Riderdata>
        var it: Iterator<String> = list.keys.iterator()
        var cnt = 0
        var itemp: ConcurrentHashMap<Int, Riderdata> = ConcurrentHashMap()
        while (it.hasNext()) {
            var ctemp = it.next()
            itemp[cnt] = list[ctemp]!!
            cnt++
        }

        if (itemp.keys.size < items!!.keys.size) {
            for (i in itemp.keys.size..items!!.keys.size) {
                items!!.remove(i)
            }
        }

        itemp!!.let { items!!.putAll(it) }
        onCreate()
    }

    fun ListClick(pos: Int) {
        Vars.MapHandler!!.obtainMessage(Finals.MAP_MOVE_FOCUS, items!![pos]).sendToTarget()
    }

    fun clickClose(){
        Vars.MapHandler!!.obtainMessage(Finals.MAP_FOR_DOPEN).sendToTarget()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()

        if(items?.keys?.size!! > 0 && !first)
        {
            Vars.MapHandler!!.obtainMessage(Finals.MAP_MOVE_FOCUS, items?.get(0)).sendToTarget()
            first = true
        }
    }

    fun getSelectBrife(): Int{
        return Finals.SELECT_BRIFE
    }

    fun getCnt(pos: Int): String? {
        return items!![pos]?.assignCount
    }

    fun getTitle(pos: Int): String? {
        return items!![pos]?.name
    }

    // 사용할일이 있을걸?
//    fun setUse(pos: Int){
//        Log.e("UsePos","" + pos + " // " + items!![pos]!!.use)
//        items!![pos]!!.use = items!![pos]!!.use != true
//        onCreate()
//    }
}