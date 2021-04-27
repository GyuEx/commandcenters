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
import kotlin.collections.HashMap
import kotlin.concurrent.timer

class SubRiderViewModel : ViewModel() {
    var Realitems: ConcurrentHashMap<String,ConcurrentHashMap<String, Riderdata>>? = null
    var items: ConcurrentHashMap<Int,Riderdata>? = null
    var adapter: RecyclerAdapterSubRider? = null

    init {
        Log.e("Memo", "Memo call")

        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(HashMap<Int,Riderdata>()))
        }
        if (Realitems == null)
        {
            Realitems = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapterSubRider(this)
        }

        Vars.SubRiderHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("MMMMMM",msg.what.toString())
                if (msg.what == Finals.INSERT_RIDER) insertLogic()
            }
        }
        insertLogic()
    }

    fun insertLogic()
    {
        Vars.riderList!!.let { Realitems!!.putAll(it) }

        var it : Iterator<String> = Realitems!!.keys.iterator()
        var cnt = 0
        var itemp : ConcurrentHashMap<Int,Riderdata> = ConcurrentHashMap()
        while (it.hasNext())
        {
            var ctemp = Realitems!![it.next()]
            var rit : Iterator<String> = ctemp!!.keys.iterator()
            while (rit.hasNext())
            {
                var rittemp = rit.next()
//                    if(Vars.f_center.size > 0)
//                    { // 속도차이가 날수있을것 같아서 차후에 검토
                    if(Vars.f_center.contains(ctemp[rittemp]?.centerID) || ctemp[rittemp]?.workingStateCode != Codes.RIDER_ON_WORK)
                    {
                        continue
                    }
                    else
                    {
                        itemp.put(cnt,ctemp[rittemp]!!)
                        cnt++
                    }
//                    }
//                    else
//                    {
//                        ctemp[rittemp]?.let { it1 -> itemp.put(cnt, it1) }
//                        cnt++
//                    }
            }
        }

        if(itemp.keys.size < items!!.keys.size)
        {
            for(i in itemp.keys.size..items!!.keys.size)
            {
                items!!.remove(i)
            }
        }

        itemp!!.let { items!!.putAll(it) }
        onCreate()
    }

    fun ListClick(pos: Int) {

    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()

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