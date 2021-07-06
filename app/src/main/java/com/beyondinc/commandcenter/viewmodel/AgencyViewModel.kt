package com.beyondinc.commandcenter.viewmodel

import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.adapter.RecyclerAdapter
import com.beyondinc.commandcenter.adapter.RecyclerAdapterAgency
import com.beyondinc.commandcenter.adapter.RecyclerAdapterHistory
import com.beyondinc.commandcenter.adapter.RecyclerAdapterPopup
import com.beyondinc.commandcenter.data.Dialogdata
import com.beyondinc.commandcenter.data.Historydata
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.repository.database.entity.Agencydata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

class AgencyViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int, Agencydata>? = null // 리스트에 보여줄 목록
    var adapter: RecyclerAdapterAgency? = null // 리스트 어뎁터
    var sendeditem : MutableLiveData<Agencydata> = MutableLiveData()
    var scrolls = MutableLiveData<Int>() // 해당부분에 값을 true로 지정하면 뷰의 스크롤을 최상단으로 올림

    init {
        Log.e("Agency", "Init다!!")
        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterAgency(this)
        }
    }

    fun insertLogic()
    {
        clear()

        var ita : Iterator<String> = Vars.agencyList!!.keys.iterator()
        var cnt = 0
        while (ita.hasNext())
        {
            var itat = ita.next()
            items!![cnt] = Vars.agencyList!![itat]!!
            cnt++
        }
        adapter!!.notifyDataSetChanged()
    }

    fun close() {

    }

    fun click_Up(){
        scrolls.value =+ 1
    }

    fun click_AgencyCall(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEND_TELEPHONE,0,sendeditem.value!!.Phone).sendToTarget()
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
        if(!items!![pos]!!.setUse)
        {
            for(i in 0 until items!!.keys.size)
            {
                items!![i]!!.setUse = false
            }
            items!![pos]!!.setUse = true
            sendeditem.value = items?.get(pos)
        }
        else
        {
            items!![pos]!!.setUse = false
            sendeditem.value = null
        }
        onCreate()
    }

    fun click_detail()
    {
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN, Finals.AGENCY_ITEM_SELECT, 0,sendeditem.value).sendToTarget()
    }

    fun click_assign(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN, Finals.NEW_ASSIGN,0,sendeditem.value).sendToTarget()
    }

    fun onResume() {}

    fun getName(pos: Int): String? {
        return items!![pos]!!.AgencyName
    }

    fun getAddr(pos: Int): String? {
        return items!![pos]!!.Addr
    }

    fun getTime(pos: Int): String? {
        return items!![pos]!!.LatestLoginDT
    }

    fun getMoney(pos: Int): String? {
        return items!![pos]!!.SumAmt
    }

    fun getLogins(pos: Int): String? {
        return items!![pos]!!.AgencyLoginYn
    }

    fun getInfo(pos: Int): String? {
        return items!![pos]!!.Maintenance
    }

    fun getUse(pos: Int): Boolean? {
        return items!![pos]!!.setUse
    }

    fun click_call_tel(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEND_TELEPHONE,0,sendeditem.value!!.Phone).sendToTarget()
    }

    fun click_call_phone(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEND_TELEPHONE,0,sendeditem.value!!.ContactMobile).sendToTarget()
    }

    fun onItemSelectedDeliveryExtFeeType(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedDepositYn(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedShareOrderYn(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedState(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedAgencyColor(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedAgencyWorkState(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedAgencyMonthlyOrderLimitYn(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedAgencyDailyOrderLimitYn(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedSurchargeByAgencySettingYn(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedWarningDepositByAgencySettingYn(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

    fun onItemSelectedMinDepositByAgencySettingYn(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        (parent.getChildAt(0) as TextView).textSize = 18f
        (parent.getChildAt(0) as TextView).setTextColor(Vars.mContext!!.getColor(R.color.black))
    }

}