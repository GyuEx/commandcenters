package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterStore
import com.beyondinc.commandcenter.repository.database.entity.Agencydata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class StoreViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int, Agencydata>? = null
    var adapter: RecyclerAdapterStore? = null
    var searchtxt = MutableLiveData<String>()

    init {
        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterStore(this)
        }
        searchtxt.value = ""
        insertBrief()
    }

    fun afterTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
    {
        searchtxt.value = s.toString()
        insertBrief()
    }

    fun insertBrief() {

        var it : Iterator<String> = Vars.orderList!!.keys.iterator()
        var tempmap = ConcurrentHashMap<String, Agencydata>()
        while (it.hasNext())
        {
            var rittemp = it.next()
            items!!.clear()
            if (tempmap.containsKey(Vars.orderList[rittemp]?.AgencyName))
            else {
                val memo = Agencydata()
                memo.agencyName = Vars.orderList[rittemp]!!.AgencyName
                tempmap[Vars.orderList[rittemp]!!.AgencyName] = memo
            }

            if (Vars.orderList[rittemp]?.DeliveryStateName == "접수") tempmap[Vars.orderList[rittemp]?.AgencyName]!!.v1++
            else if (Vars.orderList[rittemp]?.DeliveryStateName == "배정") tempmap[Vars.orderList[rittemp]?.AgencyName]!!.v2++
            else if (Vars.orderList[rittemp]?.DeliveryStateName == "픽업") tempmap[Vars.orderList[rittemp]?.AgencyName]!!.v3++
        }

        var ita : Iterator<String> = tempmap.keys.iterator()
        var cnt = 0
        items!!.clear()
        while (ita.hasNext())
        {
            var itat = ita.next()
            if(searchtxt.value!!.isEmpty() || tempmap[itat]?.agencyName!!.toLowerCase().contains(searchtxt.value!!))
            {
                tempmap!![itat]!!.id = cnt
                items!![cnt] = tempmap!![itat]!!
                cnt++
            }
        }

        onCreate()
    }

    fun onClick(pos: Int){
        Vars.ItemHandler!!.obtainMessage(Finals.STORE_ITEM_SELECT, items!![pos]!!.agencyName).sendToTarget()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun getName(pos: Int): String? {
        return items!![pos]?.agencyName
    }

    fun getVelue1(pos: Int): String? {
        return items!![pos]?.v1.toString()
    }

    fun getVelue2(pos: Int): String? {
        return items!![pos]?.v2.toString()
    }

    fun getVelue3(pos: Int): String? {
        return items!![pos]?.v3.toString()
    }

    fun close(){
        Vars.MainsHandler!!.obtainMessage(Finals.CLOSE_DIALOG).sendToTarget()
    }
}