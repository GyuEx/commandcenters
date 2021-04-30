package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapter
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap
import kotlin.concurrent.timer

class ItemViewModel : ViewModel() {
    var Realitems: ConcurrentHashMap<String, Orderdata>? = null
    var items: ConcurrentHashMap<Int, Orderdata>? = null
    var passList : ConcurrentHashMap<String, Orderdata> = ConcurrentHashMap()
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
            items = ConcurrentHashMap(Collections.synchronizedMap(HashMap<Int, Orderdata>()))
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
                Log.e("MMMMMM", msg.what.toString())
                if (msg.what == Finals.INSERT_ORDER) insertLogic()
                else if (msg.what == Finals.SELECT_EMPTY)
                {
                    select.postValue(Finals.SELECT_EMPTY)
                    Vars.ItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget()
                }
                else if (msg.what == Finals.SELECT_BRIFE)
                {
                    select.postValue(Finals.SELECT_BRIFE)
                    Vars.ItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget()
                }
            }
        }
    //    insertLogic()
    }

    fun StartTimer()
    {
        timerTask = timer(period = 1000)
        {
            if(time == Vars.timecnt)
            {
                Vars.MainsHandler!!.obtainMessage(Finals.CALL_RIDER).sendToTarget()
                time = 0
            }
            else
            {
                time++
            }
        }
    }

    fun insertLogic()
    {
        Vars.orderList!!.let { Realitems!!.putAll(it) }
        ////여기서 필터랑 전체 구현해야댐....///

        var cntbr : Int = 0
        var cntre : Int = 0
        var cntpi : Int = 0
        var cntco : Int = 0
        var cntca : Int = 0

        var it : Iterator<String> = Realitems!!.keys.iterator()
        var cnt = 0
        var itemp : ConcurrentHashMap<Int,Orderdata> = ConcurrentHashMap()
        while (it.hasNext())
        {
            var ctemp = it.next()

            if (Realitems!![ctemp]?.DeliveryStateName!! == "접수") cntbr++
            else if (Realitems!![ctemp]?.DeliveryStateName!! == "배정") cntre++
            else if (Realitems!![ctemp]?.DeliveryStateName!! == "픽업") cntpi++
            else if (Realitems!![ctemp]?.DeliveryStateName!! == "완료") cntco++
            else if (Realitems!![ctemp]?.DeliveryStateName!! == "취소") cntca++

            if(select.value == Finals.SELECT_BRIFE)
            {
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
            else
            {
                if (Vars.f_center.contains(Realitems!![ctemp]?.RcptCenterId) || Vars.f_five.contains(Realitems!![ctemp]?.DeliveryStateName))
                {
                    continue
                }
                else
                {
                    Log.e("ID","" + Realitems!![ctemp]!!.OrderId + " // " + Realitems!![ctemp]!!.AgencyName)
                    itemp[Realitems!![ctemp]!!.OrderId.toInt()] = Realitems!![ctemp]!!
                }
            }
        }

        var shorttmp : SortedMap<Int, Orderdata>
        if(Vars.UseTime) shorttmp = itemp.toSortedMap()
        else shorttmp = itemp.toSortedMap(reverseOrder())

        var finalMap : ConcurrentHashMap<Int, Orderdata> = ConcurrentHashMap()
        var shit : Iterator<Int> = shorttmp.keys.iterator()
        while(shit.hasNext())
        {
            var shitt = shit.next()
            finalMap[cnt] = shorttmp[shitt]!!
            cnt++
        }

        count_briefes.postValue(cntbr)
        count_recive.postValue(cntre)
        count_pikup.postValue(cntpi)
        count_complete.postValue(cntco)
        count_cancel.postValue(cntca)

        var forstr = "배:${cntre} 픽:${cntpi} 완:${cntco}"

        Vars.MainsHandler!!.obtainMessage(Finals.INSERT_ORDER_COUNT, forstr).sendToTarget()

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
    }

    fun onCreate() {

        var multi : Int = 0

        for(i in 0 until items!!.keys.size)
        {
            if(items!![i]!!.use!! && select.value == Finals.SELECT_BRIFE) multi++
            else if(select.value != Finals.SELECT_BRIFE)
            {
                items!![i]!!.use = false
                multi = 0
            }
        }

        Vars.multiSelectCnt = multi

        adapter!!.notifyDataSetChanged()


        //로그인 프로세스 마지막, 타이머를 켬
        if(!Logindata.OrderList)
        {
            Logindata.OrderList = true
            StartTimer()
        }
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
        Log.e("UsePos", "" + pos + " // " + items!![pos]!!.use)
        items!![pos]!!.use = items!![pos]!!.use != true
        onCreate()
    }

    fun click_brief_filter(){
        if(state_brifes.value == true)
        {
            state_brifes.postValue(false)
            Vars.f_five.add("접수")
            insertLogic()
        }
        else
        {
            state_brifes.postValue(true)
            Vars.f_five.remove("접수")
            insertLogic()
        }
    }

    fun click_recive_filter(){
        if(state_recive.value == true)
        {
            state_recive.postValue(false)
            Vars.f_five.add("배정")
            insertLogic()
        }
        else
        {
            state_recive.postValue(true)
            Vars.f_five.remove("배정")
            insertLogic()
        }
    }

    fun click_picup_filter(){
        if(state_pikup.value == true)
        {
            state_pikup.postValue(false)
            Vars.f_five.add("픽업")
            insertLogic()
        }
        else
        {
            state_pikup.postValue(true)
            Vars.f_five.remove("픽업")
            insertLogic()
        }
    }

    fun click_complete_filter(){
        if(state_complete.value == true)
        {
            state_complete.postValue(false)
            Vars.f_five.add("완료")
            insertLogic()
        }
        else
        {
            state_complete.postValue(true)
            Vars.f_five.remove("완료")
            insertLogic()
        }
    }

    fun click_cancel_filter(){
        if(state_cancel.value == true)
        {
            state_cancel.postValue(false)
            Vars.f_five.add("취소")
            insertLogic()
        }
        else
        {
            state_cancel.postValue(true)
            Vars.f_five.remove("취소")
            insertLogic()
        }
    }

    fun onLongClickOnHeading(v: View?,pos:Int): Boolean {
        Vars.MainsHandler!!.obtainMessage(Finals.ORDER_ITEM_SELECT, items?.get(pos)).sendToTarget()
        return true
    }
}