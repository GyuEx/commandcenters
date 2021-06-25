package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterRider
import com.beyondinc.commandcenter.repository.database.entity.AgencyRiderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class RiderViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int, AgencyRiderdata>? = null // 보여줄 아이템 목록
    var adapter: RecyclerAdapterRider? = null // 리스트 어뎁터
    var searchtxt = MutableLiveData<String>() //  검색텍스트

    init {
        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterRider(this)
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
        var tempmap = ConcurrentHashMap<String, AgencyRiderdata>()
        while (it.hasNext())
        {
            var rittemp = it.next()

            if(Vars.f_center.contains(Vars.orderList[rittemp]?.RcptCenterId))
            {
                continue
            }

            if (tempmap.containsKey(Vars.orderList[rittemp]?.RiderName) || Vars.orderList[rittemp]?.RiderName == "")
            else {
                val memo = AgencyRiderdata()
                memo.riderName = Vars.orderList[rittemp]!!.RiderName
                memo.centerId = Vars.riderList[Vars.orderList[rittemp]!!.RiderId!!]!!.centerID!!
                tempmap[Vars.orderList[rittemp]!!.RiderName] = memo
            }

            if (Vars.orderList[rittemp]?.DeliveryStateName == "배정") tempmap[Vars.orderList[rittemp]?.RiderName]!!.v1++
            else if (Vars.orderList[rittemp]?.DeliveryStateName == "픽업") tempmap[Vars.orderList[rittemp]?.RiderName]!!.v2++
            else if (Vars.orderList[rittemp]?.DeliveryStateName == "완료") tempmap[Vars.orderList[rittemp]?.RiderName]!!.v3++
        }

        var ita : Iterator<String> = tempmap.keys.iterator()
        var cnt = 0
        items!!.clear()
        while (ita.hasNext())
        {
            var itat = ita.next()
            if(searchtxt.value!!.isEmpty() || tempmap[itat]?.riderName!!.toLowerCase().contains(searchtxt.value!!))
            {
                tempmap!![itat]!!.id = cnt
                items!![cnt] = tempmap!![itat]!!
                cnt++
            }
        }

        onCreate()
    }

    fun onClick(pos: Int){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.RIDER_ITEM_SELECT,0, items!![pos]!!.riderName).sendToTarget()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun getName(pos: Int): String? {
        return "${Vars.centerList[items!![pos]?.centerId]?.centerName}]${items!![pos]?.riderName}"
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
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_DIALOG,0).sendToTarget()
    }
}