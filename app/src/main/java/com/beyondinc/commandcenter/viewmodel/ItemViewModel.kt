package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.adapter.RecyclerAdapter
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.MakeJsonParam
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import org.json.simple.JSONArray
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap
import kotlin.concurrent.timer

class ItemViewModel : ViewModel() {
    var Realitems: ConcurrentHashMap<String,ConcurrentHashMap<String,Orderdata>>? = null
    var items: ConcurrentHashMap<Int,Orderdata>? = null
    var adapter: RecyclerAdapter? = null

    var state_brifes = MutableLiveData<Boolean>()
    var state_recive = MutableLiveData<Boolean>()
    var state_pikup = MutableLiveData<Boolean>()
    var state_complete = MutableLiveData<Boolean>()
    var state_cancel = MutableLiveData<Boolean>()

    var count_briefes = MutableLiveData<Int>()
    var count_recive = MutableLiveData<Int>()
    var count_pikup = MutableLiveData<Int>()
    var count_complete = MutableLiveData<Int>()
    var count_cancel = MutableLiveData<Int>()

    var select = MutableLiveData<Int>()

    var time = 0
    var timerTask : Timer? = null

    init {
        Log.e("Memo", "Memo call")

        //필터 초기값 설정
        state_brifes.postValue(true)
        state_recive.postValue(true)
        state_pikup.postValue(true)
        state_complete.postValue(true)
        state_cancel.postValue(true)

        count_briefes.postValue(0)
        count_recive.postValue(0)
        count_pikup.postValue(0)
        count_complete.postValue(0)
        count_cancel.postValue(0)

        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(HashMap<Int,Orderdata>()))
        }
        if (Realitems == null)
        {
            Realitems = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapter(this)
        }

        select.postValue(Finals.SELECT_EMPTY)

        Vars.ItemHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("MMMMMM",msg.what.toString())
                if (msg.what == Finals.INSERT_ORDER) insertLogic()
                else if (msg.what == Finals.SELECT_EMPTY)
                {
                    select.postValue(Finals.SELECT_EMPTY)
                    onCreate()
                }
                else if (msg.what == Finals.SELECT_BRIFE)
                {
                    select.postValue(Finals.SELECT_BRIFE)
                    onCreate()
                }
            }
        }
        insertLogic()
    }

    fun StartTimer()
    {
        timerTask = timer(period = 1000)
        {
            if(time == Vars.timecnt)
            {
                Log.e("Timer","Live Timer ==== $time")
                Vars.MainsHandler!!.obtainMessage(Finals.CALL_ORDER).sendToTarget()
                time = 0
            }
            else
            {
                Log.e("Timer","Live Timer ==== $time")
                time++
            }
        }
    }

    fun insertLogic()
    {
        Vars.orderList!!.let { Realitems!!.putAll(it) }
        ////여기서 필터랑 전체 구현해야댐....///
        if(select.value == Finals.SELECT_EMPTY)
        {
            var it : Iterator<String> = Realitems!!.keys.iterator()
            var cnt = 0
            var itemp : ConcurrentHashMap<Int,Orderdata> = ConcurrentHashMap()
            while (it.hasNext())
            {
                var ctemp = Realitems!![it.next()]
                var rit : Iterator<String> = ctemp!!.keys.iterator()
                while (rit.hasNext())
                {
                    ctemp[rit.next()]?.let { it1 -> itemp.put(cnt, it1) }
                    cnt++
                }
            }
            itemp!!.let { items!!.putAll(it) }
            onCreate()
        }
    }

    fun ListClick() {
        (Vars.mContext as MainsFun).showOderdetail()
    }

    fun onCreate() {

        var cntbr : Int = 0
        var cntre : Int = 0
        var cntpi : Int = 0
        var cntco : Int = 0
        var cntca : Int = 0

        for(i in 0 until items!!.keys.size)
        {
            if(items!![i]!!.ApprovalTypeName!! == "접수") cntbr++
            else if (items!![i]!!.ApprovalTypeName!! == "배정") cntre++
            else if (items!![i]!!.ApprovalTypeName!! == "픽업") cntpi++
            else if (items!![i]!!.ApprovalTypeName!! == "완료") cntco++
            else if (items!![i]!!.ApprovalTypeName!! == "취소") cntca++
        }

        count_briefes.postValue(cntbr)
        count_recive.postValue(cntre)
        count_pikup.postValue(cntpi)
        count_complete.postValue(cntco)
        count_cancel.postValue(cntca)

        adapter!!.notifyDataSetChanged()


        //로그인 프로세스 마지막, 타이머를 켬
        if(!Logindata.OrderList)
        {
            Logindata.OrderList = true
            StartTimer()
        }
    }

    fun onResume() {}

//    fun getItems(flag:Int): HashMap<Int, Orderdata>? {
//        return items
//    }

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
        return items!![pos]?.DeliveryDistance
    }

    fun getWork(pos: Int): String? {
        return items!![pos]?.PickupDT
    }

    fun getPaywon(pos: Int): String? {
        return items!![pos]?.DeliveryFee
    }

    fun setUse(pos: Int){
        Log.e("UsePos","" + pos + " // " + items!![pos]!!.use)
        //items!![pos]!!.use = items!![pos]!!.use != true
        onCreate()
    }

    fun click_brief_filter(){
        if(state_brifes.value == true)state_brifes.postValue(false)
        else state_brifes.postValue(true)
    }

    fun click_recive_filter(){
        if(state_recive.value == true)state_recive.postValue(false)
        else state_recive.postValue(true)
    }

    fun click_picup_filter(){
        if(state_pikup.value == true)state_pikup.postValue(false)
        else state_pikup.postValue(true)
    }

    fun click_complete_filter(){
        if(state_complete.value == true)state_complete.postValue(false)
        else state_complete.postValue(true)
    }

    fun click_cancel_filter(){
        if(state_cancel.value == true)state_cancel.postValue(false)
        else state_cancel.postValue(true)
    }
}