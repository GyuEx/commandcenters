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
        Log.e("Memo", "Memo call")

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
                Log.e("MMMMMM",msg.what.toString())
                if (msg.what == Finals.INSERT_ORDER) insertLogic()
                else if(msg.what == Finals.SELECT_ORDER) select.postValue(Finals.SELECT_ORDER)
                else if(msg.what == Finals.SELECT_EMPTY) select.postValue(Finals.SELECT_EMPTY)
            }
        }
        insertLogic()
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
                Log.e("ID", "" + Realitems!![ctemp]!!.OrderId + " // " + Realitems!![ctemp]!!.AgencyName)
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

        Log.e("UsePos", "" + pos + " // " + items!![pos]!!.use)
        items!![pos]!!.use = items!![pos]!!.use != true
        onCreate()
//        var toast : Toast = Toast.makeText(Vars.mContext, "선택된 라이더가 없습니다.", Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.BOTTOM,0,600)
//        toast.show()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun getSelectBrife(): Int{
        return Finals.SELECT_BRIFE
    }

    fun getUsetime(pos: Int): String? {
        return items!![pos]?.AgencyRequestTime
    }

    fun getResttime(pos: Int): String? {
        return items!![pos]?.AgencyRequestTime
    }

    fun getPay(pos: Int): String? {
        return items!![pos]?.ApprovalTypeName
    }

    fun getTitle(pos: Int): String? {
        return items!![pos]?.AgencyName
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