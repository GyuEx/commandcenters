package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterSub
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap
import kotlin.concurrent.timer

class SubItemViewModel : ViewModel() {
    var Realitems: ConcurrentHashMap<String,Orderdata>? = null
    var items: ConcurrentHashMap<Int,Orderdata>? = null
    var adapter: RecyclerAdapterSub? = null

    var select = MutableLiveData<Int>()

    init {
        Log.e("aaaa", "Sub Item view model Init")
        select.postValue(Finals.SELECT_EMPTY)

        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(HashMap<Int,Orderdata>()))
        }
        if (Realitems == null)
        {
            Realitems = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapterSub(this)
        }

        Vars.SubItemHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("SubItemHandler",msg.what.toString())
                if (msg.what == Finals.INSERT_ORDER) insertLogic()
                else if(msg.what == Finals.SELECT_ORDER) select.postValue(Finals.SELECT_ORDER)
                else if(msg.what == Finals.SELECT_EMPTY) selectEmpte()
                else if(msg.what == Finals.ORDER_ASSIGN_LIST) orderassignlist(msg.obj as String)
            }
        }
        //insertLogic()
    }

    fun orderassignlist(id : String){
        var makeHash = HashMap<String,ArrayList<String>>()
        var makeArray = ArrayList<String>()
        for(i in 0 until items!!.keys.size)
        {
            if(items!![i]!!.use)
            {
                makeArray.add(items!![i]!!.OrderId)
            }
        }
        if(makeArray.size < 1) Vars.MainsHandler!!.obtainMessage(Finals.ORDER_TOAST_SHOW,"선택된 오더가 없습니다.").sendToTarget()
        else makeHash[id] = makeArray
        Vars.MainsHandler!!.obtainMessage(Finals.ORDER_ASSIGN_LIST, makeHash).sendToTarget()
    }

    fun insertLogic()
    {
        Vars.orderList!!.let { Realitems!!.putAll(it) }

        var it : Iterator<String> = Realitems!!.keys.iterator()
        var cnt = 0
        var itemp : ConcurrentHashMap<Int,Orderdata> = ConcurrentHashMap()
        while (it.hasNext())
        {
            var ctemp = it.next()

            if(Vars.f_center.contains(Realitems!![ctemp]?.RcptCenterId) || Realitems!![ctemp]?.DeliveryStateName != "접수")
            {
                continue
            }
            else
            {
                itemp[Realitems!![ctemp]!!.OrderId.toInt()] = Realitems!![ctemp]!!
            }
        }

        var shorttmp : SortedMap<Int, Orderdata>
        if(Vars.UseTime) shorttmp = itemp.toSortedMap()
        else shorttmp = itemp.toSortedMap(reverseOrder())

        var finalMap : ConcurrentHashMap<Int,Orderdata> = ConcurrentHashMap()
        var shit : Iterator<Int> = shorttmp.keys.iterator()
        while(shit.hasNext())
        {
            var shitt = shit.next()
            finalMap[cnt] = shorttmp[shitt]!!
            cnt++
        }

        if(finalMap.keys.size < items!!.keys.size)
        {
            for(i in finalMap.keys.size..items!!.keys.size)
            {
                items!!.remove(i)
            }
        }

        finalMap!!.let { items!!.putAll(it) }

        onCreate()
    }

    fun ListClick(pos: Int) {

        items!![pos]!!.use = items!![pos]!!.use != true
        Log.e("UsePos", "" + pos + " // " + items!![pos]!!.use)
        onCreate()
    }

    fun onCreate() {

        var cnt = 0
        for(i in 0 until items?.keys!!.size)
        {
            if(items!![i]!!.use)cnt++
        }

        if(cnt > 0) Vars.MapHandler!!.obtainMessage(Finals.SELECT_ORDER).sendToTarget()
        else Vars.MapHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()

        adapter!!.notifyDataSetChanged()
    }

    fun getSelectBrife(): Int{
        return Finals.SELECT_BRIFE
    }

    fun selectEmpte()
    {
        select.value = Finals.SELECT_EMPTY
        for(i in 0 until items?.keys!!.size)
        {
            items!![i]!!.use = false
            Log.e("UsePos", "" + i + " // " + items!![i]!!.use)
        }
        onCreate()
    }

    fun getUsetime(pos: Int): String? {
        return items!![pos]?.AgencyRequestTime
    }

    fun getResttime(pos: Int): String? {
        //오더시간을 계산해보자
        var a : String = "0"
        if(items!![pos]!!.DeliveryStateName == "배정") {
            var ft = SimpleDateFormat("HH:mm:ss")
            var now = ft.parse(ft.format(Date()))
            var nt = ft.parse(ft.format(ft.parse(items!![pos]!!.DriverAssignDT)))
            a = ((now.time - nt.time)/60000).toString()
        }
        else if(items!![pos]!!.DeliveryStateName == "접수")
        {
            a = "0"
        }
        else if(items!![pos]!!.DeliveryStateName == "픽업")
        {
            var ft = SimpleDateFormat("HH:mm:ss")
            var nt = ft.parse(ft.format(ft.parse(items!![pos]!!.DriverAssignDT)))
            var pt = ft.parse(ft.format(ft.parse(items!![pos]!!.PickupDT)))
            a = ((pt.time - nt.time)/60000).toString()
        }
        return a
    }

    fun getPay(pos: Int): String? {
        return items!![pos]?.ApprovalTypeName
    }

    fun getTitle(pos: Int): String? {
        var s : String = ""
        if(Vars.Usenick) s = Vars.centerNick[items!![pos]?.CenterName] + "] " + items!![pos]?.AgencyName
        else s = items!![pos]?.CenterName + "] " + items!![pos]?.AgencyName
        return s
    }

    fun getAdress(pos: Int): String? {
        return items!![pos]?.CustomerShortAddr
    }

    fun getPoi(pos: Int): String? {
        return items!![pos]?.CustomerDetailAddr
    }

    fun getRider(pos: Int): String? {
        return if(items!![pos]?.DeliveryStateName=="접수") items!![pos]?.DeliveryDistance
        else(items!![pos]?.RiderName)
    }

    fun getWork(pos: Int): String? {
        return items!![pos]?.DeliveryStateName
    }

    fun getPaywon(pos: Int): String? {
        return items!![pos]?.DeliveryFee
    }

    fun setUse(pos: Int){
        Log.e("UsePos","" + pos + " // " + items!![pos]!!.use)
        items!![pos]!!.use = items!![pos]!!.use != true
        onCreate()
    }
}