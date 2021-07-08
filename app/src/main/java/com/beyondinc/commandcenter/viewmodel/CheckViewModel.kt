package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterCheck
import com.beyondinc.commandcenter.data.Checkdata
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.handler.MarkerThread
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class CheckViewModel : ViewModel() {

    var items: ConcurrentHashMap<Int, Checkdata>? = null // 리스트에 보여줄 목록
    var adapter: RecyclerAdapterCheck? = null // 리스트 어뎁터
    var allcheck = MutableLiveData<Boolean>() // 전체선택, 전체해제 여부 저장

    init {
        Log.e("CheckView", "CheckView Enable")

        allcheck.value = true

        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterCheck(this)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("CheckViewModel","Check View Model Cleared")
    }

    fun insertStore() {
        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var cnt = 0
        while (it.hasNext())
        {
            var tmp = Vars.centerList[it.next()]
            //Log.e("insert", "" + tmp!!.centerName + "\n")
            val memo = Checkdata()
            memo.id = cnt
            memo.centerId = tmp?.centerId
            memo.title = tmp?.centerName
            memo.use = true
            items!![memo.id] = memo
            cnt++
        }

        onCreate()

        //최초1회 센터리스트가 완료되었다.
        if(!Logindata.CenterList)
        {
            val pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
            var it : Iterator<String> = Vars.centerList.keys.iterator()
            while(it.hasNext())
            {
                var itt = it.next()
                var str = pref.getString(Vars.centerList[itt]!!.centerName,"")
                Vars.centerNick[Vars.centerList[itt]!!.centerName!!] = str.toString()
            }

            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CALL_RIDER,0).sendToTarget()
            Logindata.CenterList = true
        }
    }

    fun ListClick() {

    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun onResume() {}


    fun getTitle(pos: Int): String? {
        return if(Vars.centerNick.containsKey(items!![pos]?.title)) //키가 없을수도 있음
        {
            if(!Vars.centerNick[items!![pos]?.title].isNullOrEmpty()) items!![pos]?.title + " // " + Vars.centerNick[items!![pos]?.title] //값이 없을수도 있음
            else items!![pos]?.title
        } else items!![pos]?.title
    }

    fun setUse(pos: Int){
        items!![pos]?.use = items!![pos]?.use != true
        onCreate()
    }

    fun allEnable(){
        for(i in 0 until items!!.size)
        {
            items!![i]?.use = true
        }
        allcheck.value = true
        onCreate()
    }
    fun allDisable(){
        for(i in 0 until items!!.size)
        {
            items!![i]?.use = false
        }
        allcheck.value = false
        onCreate()
    }

    fun click_Success()
    {
        Vars.f_center.clear()
        for(i in 0 until items!!.size)
        {
            if(items!![i]!!.use == false) Vars.f_center.add(items!![i]!!.centerId.toString())
        }
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget() //리스트 새로그리고
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.INSERT_ORDER,0).sendToTarget() //리스트 새로그리고
        MarkerThread().start() // 마커도 새로생성하고 마 다햇서
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_CHECK,0).sendToTarget() //뷰 닫기
    }

    fun click_cancel()
    {
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_CHECK,0).sendToTarget()
    }
}