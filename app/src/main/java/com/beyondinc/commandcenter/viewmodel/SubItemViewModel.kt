package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterSub
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

class SubItemViewModel : ViewModel() {
    var itemss: ConcurrentHashMap<Int,Orderdata>? = null
    var Realitemss: ConcurrentHashMap<String,Orderdata>? = null
    var adapter: RecyclerAdapterSub? = null

    var select = MutableLiveData<Int>()

    init {
        //Log.e("aaaa", "Sub Item view model Init")
        select.value = Finals.SELECT_EMPTY

        if (itemss == null) {
            itemss = ConcurrentHashMap()
        }
        if (Realitemss == null) {
            Realitemss = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterSub(this)
        }

        Vars.SubItemHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                //Log.e("SubItemHandler",msg.what.toString())
                if (msg.what == Finals.INSERT_ORDER) insertLogic()
                else if(msg.what == Finals.SELECT_ORDER) select.value = Finals.SELECT_ORDER
                else if(msg.what == Finals.SELECT_EMPTY) selectEmpte()
                else if(msg.what == Finals.ORDER_ASSIGN_LIST) orderassignlist(msg.obj as String)
            }
        }
    }

    fun orderassignlist(id : String){
        var makeHash = ConcurrentHashMap<String,ArrayList<String>>()
        var makeArray = ArrayList<String>()
        for(i in 0 until itemss!!.keys.size)
        {
            if(itemss!![i]!!.use)
            {
                makeArray.add(itemss!![i]!!.OrderId)
            }
        }
        if(makeArray.size < 1) Vars.MainsHandler!!.obtainMessage(Finals.ORDER_TOAST_SHOW,"선택된 오더가 없습니다.").sendToTarget()
        else makeHash[id] = makeArray
        Vars.MainsHandler!!.obtainMessage(Finals.ORDER_ASSIGN_LIST, makeHash).sendToTarget()
    }

    fun insertLogic()
    {
        Vars.orderList!!.let { Realitemss!!.putAll(it) }

        var cntbr : Int = 0
        var cntre : Int = 0
        var cntpi : Int = 0
        var cntco : Int = 0
        var cntca : Int = 0

        var it : Iterator<String> = Realitemss!!.keys.iterator()
        var cnt = 0
        var itemp : ConcurrentHashMap<Int,Orderdata> = ConcurrentHashMap()
        while (it.hasNext())
        {
            var ctemp = it.next()

            if (Realitemss!![ctemp]?.DeliveryStateName!! == "접수") cntbr++
            else if (Realitemss!![ctemp]?.DeliveryStateName!! == "배정") cntre++
            else if (Realitemss!![ctemp]?.DeliveryStateName!! == "픽업") cntpi++
            else if (Realitemss!![ctemp]?.DeliveryStateName!! == "완료") cntco++
            else if (Realitemss!![ctemp]?.DeliveryStateName!! == "취소") cntca++

            if(Vars.f_center.contains(Realitemss!![ctemp]?.RcptCenterId) || Realitemss!![ctemp]?.DeliveryStateName != "접수")
            {
                continue
            }
            else
            {
                itemp[Realitemss!![ctemp]!!.OrderId.toInt()] = Realitemss!![ctemp]!!
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

        var forstr = "배:${cntre} 픽:${cntpi} 완:${cntco}"

        Vars.MainsHandler!!.obtainMessage(Finals.INSERT_ORDER_COUNT, forstr).sendToTarget()
        Vars.MapHandler!!.obtainMessage(Finals.INSERT_ORDER_COUNT,cntbr).sendToTarget()

        if(finalMap.keys.size < itemss!!.keys.size)
        {
            for(i in finalMap.keys.size..itemss!!.keys.size)
            {
                itemss!!.remove(i)
            }
        }

        finalMap!!.let {itemss!!.putAll(it)}

        onCreate()
    }

    fun ListClick(pos: Int) {

        if(itemss!![pos]!!.use)
        {
            itemss!![pos]!!.use = false
            Vars.MapHandler!!.obtainMessage(Finals.MAP_FOR_ASSIGN_REMOVE, itemss!![pos]!!).sendToTarget()
        }
        else
        {
            itemss!![pos]!!.use = true
            Vars.MapHandler!!.obtainMessage(Finals.MAP_FOR_ASSIGN_CREATE, itemss!![pos]!!).sendToTarget()
        }
        onCreate()
    }

    fun onCreate() {

        Vars.MapHandler!!.obtainMessage(Finals.INSERT_ORDER_COUNT,itemss!!.keys.size).sendToTarget()

        var cnt = 0
        for(i in 0 until itemss!!.keys.size)
        {
            if(itemss!![i]!!.use!! && select.value == Finals.SELECT_ORDER) cnt++
            else if(select.value != Finals.SELECT_ORDER)
            {
                itemss!![i]!!.use = false
                cnt = 0
            }
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
        //Log.e("헛","설마 엠피티가 호출 되는감?!")
        select.value = Finals.SELECT_EMPTY
        for(i in 0 until itemss?.keys!!.size)
        {
            itemss!![i]!!.use = false
        }
        onCreate()
    }

    fun getUsetime(pos: Int): String? {
        return itemss!![pos]?.AgencyRequestTime
    }

    fun getResttime(pos: Int): String? {
        //오더시간을 계산해보자 -> 접수건은 계산할 필요가 없음
//        var a : String = "0"
//        if(items!![pos]!!.DeliveryStateName == "배정") {
//            var ft = SimpleDateFormat("HH:mm:ss")
//            var now = ft.parse(ft.format(Date()))
//            var nt = ft.parse(ft.format(ft.parse(items!![pos]!!.DriverAssignDT)))
//            a = ((now.time - nt.time)/60000).toString()
//        }
//        else if(items!![pos]!!.DeliveryStateName == "접수")
//        {
//            a = "0"
//        }
//        else if(items!![pos]!!.DeliveryStateName == "픽업")
//        {
//            var ft = SimpleDateFormat("HH:mm:ss")
//            var nt = ft.parse(ft.format(ft.parse(items!![pos]!!.DriverAssignDT)))
//            var pt = ft.parse(ft.format(ft.parse(items!![pos]!!.PickupDT)))
//            a = ((pt.time - nt.time)/60000).toString()
//        }
        return "0"
    }

    fun getPay(pos: Int): String? {
        return itemss!![pos]?.ApprovalTypeName
    }

    fun getTitle(pos: Int): String? {
        var s : String = ""
        if(Vars.Usenick) s = Vars.centerNick[itemss!![pos]?.CenterName] + "] " + itemss!![pos]?.AgencyName
        else s = itemss!![pos]?.CenterName + "] " + itemss!![pos]?.AgencyName
        return s
    }

    fun getAdress(pos: Int): String? {
        return itemss!![pos]?.CustomerShortAddr
    }

    fun getPoi(pos: Int): String? {
        return itemss!![pos]?.CustomerDetailAddr
    }

    fun getRider(pos: Int): String? {
        return if(itemss!![pos]?.DeliveryStateName=="접수") itemss!![pos]?.DeliveryDistance
        else(itemss!![pos]?.RiderName)
    }

    fun getWork(pos: Int): String? {
        return itemss!![pos]?.DeliveryStateName
    }

    fun getPaywon(pos: Int): String? {
        return itemss!![pos]?.DeliveryFee
    }
}