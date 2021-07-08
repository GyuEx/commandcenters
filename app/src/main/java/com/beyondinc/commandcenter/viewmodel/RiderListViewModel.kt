package com.beyondinc.commandcenter.viewmodel

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.*
import com.beyondinc.commandcenter.data.RiderListdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.text.DecimalFormat
import java.util.concurrent.ConcurrentHashMap

class RiderListViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int, RiderListdata>? = null // 리스트에 보여줄 목록
    var adapter: RecyclerAdapterRiderList? = null // 리스트 어뎁터
    var sendeditem : MutableLiveData<RiderListdata> = MutableLiveData()
    var scrolls = MutableLiveData<Int>() // 해당부분에 값을 true로 지정하면 뷰의 스크롤을 최상단으로 올림

    init {
        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterRiderList(this)
        }
    }

    fun insertLogic()
    {
        clear()

        var ita : Iterator<String> = Vars.riderlistList!!.keys.iterator()
        var cnt = 0
        while (ita.hasNext())
        {
            var itat = ita.next()
            items!![cnt] = Vars.riderlistList!![itat]!!
            cnt++
        }
        adapter!!.notifyDataSetChanged()
    }

    fun close() {

    }

    fun click_Up(){
        scrolls.value =+ 1
    }

    fun clear(){
        items!!.clear()
        adapter!!.notifyDataSetChanged()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun setUse(pos:Int)
    {
        if(!items!![pos]!!.use)
        {
            for(i in 0 until items!!.keys.size)
            {
                items!![i]!!.use = false
            }
            items!![pos]!!.use = true
            sendeditem.value = items?.get(pos)
        }
        else
        {
            items!![pos]!!.use = false
            sendeditem.value = null
        }
        onCreate()
    }

    fun click_detail(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN, Finals.RIDER_ITEM_SELECT, 0,sendeditem.value).sendToTarget()
    }

    fun click_SMS(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN, Finals.SEND_SMS,0, sendeditem?.value!!.Mobile).sendToTarget()
    }

    fun click_RiderCall(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEND_TELEPHONE,0,sendeditem.value!!.Mobile).sendToTarget()
    }

    fun onResume() {}

    fun getName(pos: Int): String? {
        return items!![pos]!!.RiderName
    }

    fun getCenterName(): String? {
        var id = sendeditem.value!!.RiderId
        var centerid = Vars.riderList[id]?.centerID
        var centerName = Vars.centerList[centerid]?.centerName.toString()
        return centerName
    }

    fun getPhone(pos: Int): String? {
        return items!![pos]!!.Mobile
    }

    fun getAssingCnt(pos: Int): String? {
        if(items!![pos]!!.CurrWorkingOrderCnt.isNullOrEmpty()) return "0"
        else {
            val df = DecimalFormat("#,###")
            val Distresult = df.format(items!![pos]!!.CurrWorkingOrderCnt!!.toInt())
            return Distresult
        }
    }

    fun getAllCnt(pos: Int): String? {
        if(items!![pos]!!.TotalOrderCnt.isNullOrEmpty()) return "0"
        else {
            val df = DecimalFormat("#,###")
            val Distresult = df.format(items!![pos]!!.TotalOrderCnt!!.toInt())
            return Distresult
        }
    }

    fun getState(pos: Int): String? {
        return items!![pos]!!.State
    }

    fun getWork(pos: Int): String? {
        return items!![pos]!!.AttendanceYn
    }

    fun getExtMoney(pos: Int): String? {
        val df = DecimalFormat("#,###")
        val Distresult = df.format(items!![pos]!!.Deposit!!.toLong())
        return Distresult + "원"
    }

    fun getUse(pos: Int): Boolean? {
        return items!![pos]!!.use
    }
}