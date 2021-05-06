package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterRider
import com.beyondinc.commandcenter.data.Dialogdata
import com.beyondinc.commandcenter.repository.database.entity.AgencyRiderdata
import com.beyondinc.commandcenter.repository.database.entity.Agencydata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class RiderViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int, AgencyRiderdata>? = null
    var adapter: RecyclerAdapterRider? = null

    init {
        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterRider(this)
        }
        insertBrief()
    }

    fun insertBrief() {

        var it : Iterator<String> = Vars.orderList!!.keys.iterator()
        var tempmap = HashMap<String, AgencyRiderdata>()
        while (it.hasNext())
        {
            var rittemp = it.next()

            if(tempmap.containsKey(Vars.orderList[rittemp]?.RiderName) || Vars.orderList[rittemp]?.RiderName == "")
            else
            {
                val memo = AgencyRiderdata()
                memo.riderName = Vars.orderList[rittemp]!!.RiderName
                tempmap[Vars.orderList[rittemp]!!.RiderName] = memo
            }

            if(Vars.orderList[rittemp]?.DeliveryStateName == "배정") tempmap[Vars.orderList[rittemp]?.RiderName]!!.v1++
            else if(Vars.orderList[rittemp]?.DeliveryStateName == "픽업") tempmap[Vars.orderList[rittemp]?.RiderName]!!.v2++
            else if(Vars.orderList[rittemp]?.DeliveryStateName == "완료") tempmap[Vars.orderList[rittemp]?.RiderName]!!.v3++
        }

        var ita : Iterator<String> = tempmap.keys.iterator()
        var cnt = 0
        while (ita.hasNext())
        {
            var itat = ita.next()
            tempmap!![itat]!!.id = cnt
            items!![cnt] = tempmap!![itat]!!
            cnt++
        }

        onCreate()
    }

    fun onClick(pos: Int){
        Vars.ItemHandler!!.obtainMessage(Finals.RIDER_ITEM_SELECT,items!![pos]!!.riderName).sendToTarget()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun getName(pos: Int): String? {
        return items!![pos]?.riderName
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
}