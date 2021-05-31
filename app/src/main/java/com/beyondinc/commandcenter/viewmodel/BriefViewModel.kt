package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterPopup
import com.beyondinc.commandcenter.data.Dialogdata
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class BriefViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int, Dialogdata>? = null
    var adapter: RecyclerAdapterPopup? = null
    var searchtxt = MutableLiveData<String>()

    init {
        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterPopup(this)
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

        var it : Iterator<String> = Vars.riderList!!.keys.iterator()
        var cnt = 0
        items!!.clear()
        while (it.hasNext())
        {
            var rittemp = it.next()
            if(searchtxt.value!!.isEmpty() || Vars.riderList[rittemp]?.name!!.toLowerCase().contains(searchtxt.value!!))
            {
                if (Vars.riderList[rittemp]!!.workingStateCode == Codes.RIDER_ON_WORK) {
                    val memo = Dialogdata()
                    memo.id = cnt
                    memo.name = Vars.riderList[rittemp]!!.name
                    memo.realId = Vars.riderList[rittemp]!!.id
                    memo.velue1 = Vars.riderList[rittemp]!!.assignCount.toString()
                    memo.velue2 = Vars.riderList[rittemp]!!.pickupCount.toString()
                    memo.velue3 = Vars.riderList[rittemp]!!.completeCount.toString()
                    items!![cnt] = memo
                    cnt++
                }
            }
        }
        onCreate()
    }


    fun onClick(pos: Int){
        //Log.e("PopUp","click event" + items!![pos]!!.name)
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM, Finals.ORDER_ASSIGN, 0, items!![pos]!!.realId).sendToTarget()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun getName(pos: Int): String? {
        return items!![pos]?.name
    }

    fun getVelue1(pos: Int): String? {
        return items!![pos]?.velue1
    }

    fun getVelue2(pos: Int): String? {
        return items!![pos]?.velue2
    }

    fun getVelue3(pos: Int): String? {
        return items!![pos]?.velue3
    }

    fun close(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_DIALOG,0).sendToTarget()
    }
}