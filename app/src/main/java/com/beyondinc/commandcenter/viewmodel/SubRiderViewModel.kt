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
import java.util.concurrent.Executor
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.timer

class SubRiderViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int,Riderdata>? = null
    var adapter: RecyclerAdapterSubRider? = null

    var first: Boolean = false

    var select = MutableLiveData<Int>()
    var selectedRider : Riderdata? = null

    var searchtxt = MutableLiveData<String>()

    var list = ConcurrentHashMap<String,Riderdata>()

    init {
        Log.e("Memo", "Memo call")

        select.value = Finals.SELECT_EMPTY
        searchtxt.value = ""

        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapterSubRider(this)
        }

        Vars.SubRiderHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("SubriderHandler",msg.what.toString())
                if (msg.what == Finals.INSERT_RIDER) insertLogic(msg.obj as Riderdata)
                else if(msg.what == Finals.SELECT_RIDER) select.value = Finals.SELECT_RIDER
                else if(msg.what == Finals.SELECT_EMPTY)
                {
                    select.value = Finals.SELECT_EMPTY
                    selectedRider = null
                }
                else if(msg.what == Finals.MAP_FOR_CALL_RIDER) getRider(msg.obj)
                else if(msg.what == Finals.REMOVE_RIDER_MARKER) removeRider(msg.obj as Riderdata)
                else if(msg.what == Finals.MAP_FOR_REFRASH) refrashrider()
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
            if(items!![itt] == Mid)
            {
                Vars.MapHandler!!.obtainMessage(Finals.MAP_MOVE_FOCUS, items!![itt]).sendToTarget()
                selectedRider = items!![itt]
                break // 그만행
            }
        }
    }

    fun refrashrider(){
        if(selectedRider != null) Vars.MapHandler!!.obtainMessage(Finals.MAP_MOVE_FOCUS,selectedRider).sendToTarget()
    }

    fun removeRider(obj : Riderdata){
        list.remove(obj.id)
        onCreate()
    }

    fun afterTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
    {
        searchtxt.value = s.toString()
        onCreate()
    }

    fun insertLogic(obj : Riderdata) {
        list[obj.id!!] = obj
        onCreate()
    }

    fun ListClick(pos: Int) {
        Vars.MapHandler!!.obtainMessage(Finals.MAP_MOVE_FOCUS, items!![pos]).sendToTarget()
    }

    fun clickClose(){
        Vars.MapHandler!!.obtainMessage(Finals.MAP_FOR_DOPEN).sendToTarget()
    }

    fun onCreate() {

        var it: Iterator<String> = list.keys.iterator()
        var cnt = 0
        var itemp: ConcurrentHashMap<Int, Riderdata> = ConcurrentHashMap()

        while (it.hasNext()) {
            var ctemp = it.next()
            itemp[list[ctemp]!!.completeCount] = list[ctemp]!!
        }

        var shorttmp : SortedMap<Int, Riderdata>
        if(Vars.UseGana) shorttmp = itemp.toSortedMap()
        else shorttmp = itemp.toSortedMap(reverseOrder())

        var finalMap : ConcurrentHashMap<Int, Riderdata> = ConcurrentHashMap()
        var shit : Iterator<Int> = shorttmp.keys.iterator()

        while(shit.hasNext())
        {
            var shitt = shit.next()
            if(searchtxt.value!!.isEmpty() || shorttmp[shitt]?.name!!.toLowerCase().contains(searchtxt.value!!))
            {
                finalMap[cnt] = shorttmp[shitt]!!
                cnt++
            }
        }

        if(finalMap.keys.size < items!!.keys.size)
        {
            for(i in finalMap.keys.size..items!!.keys.size)
            {
                items!!.remove(i)
            }
        }

        finalMap!!.let { items!!.putAll(it) }

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
        return "${items!![pos]?.assignCount} / ${items!![pos]?.pickupCount}"
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