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
    var Realitems: ConcurrentHashMap<String,ConcurrentHashMap<String,Orderdata>>? = null
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

        if(select.value == Finals.SELECT_EMPTY)
        {
            var it: Iterator<String> = Realitems!!.keys.iterator()
            var cnt = 0
            var itemp: ConcurrentHashMap<Int, Orderdata> = ConcurrentHashMap()
            while (it.hasNext()) {
                var ctemp = Realitems!![it.next()]
                var rit: Iterator<String> = ctemp!!.keys.iterator()
                while (rit.hasNext()) {
                    var rittemp = rit.next()

                    if (Vars.f_center.contains(ctemp[rittemp]?.RcptCenterId) || ctemp[rittemp]?.DeliveryStateName != "접수") {
                        continue
                    } else {
                        itemp.put(cnt, ctemp[rittemp]!!)
                        cnt++
                    }
                }
            }

            if (itemp.keys.size < items!!.keys.size) {
                for (i in itemp.keys.size..items!!.keys.size) {
                    items!!.remove(i)
                }
            }

            itemp!!.let { items!!.putAll(it) }
            onCreate()
        }
    }

    fun ListClick(pos: Int) {
        var toast : Toast = Toast.makeText(Vars.mContext, "선택된 라이더가 없습니다.", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM,0,600)
        toast.show()
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