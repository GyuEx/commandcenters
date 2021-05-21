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
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class CheckViewModel : ViewModel() {

    var items: ConcurrentHashMap<Int, Checkdata>? = null
    var adapter: RecyclerAdapterCheck? = null
    var allcheck = MutableLiveData<Boolean>()

    init {
        //Log.e("CheckView", "CheckView Enable")
        allcheck.value = true

        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterCheck(this)
        }

        Vars.CheckHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == Finals.INSERT_STORE) insertStore()
            }
        }
        Vars.MainsHandler!!.obtainMessage(Finals.CALL_CENTER).sendToTarget()
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

            Vars.MainsHandler!!.obtainMessage(Finals.CALL_RIDER).sendToTarget()
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
        Vars.ItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget() //리스트 새로그리고
        Vars.SubItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget() //리스트 새로그리고
        //Vars.MapHandler!!.obtainMessage(Finals.CREATE_RIDER_MARKER).sendToTarget() //리스트 새로그리고
        Vars.MainsHandler!!.obtainMessage(Finals.CLOSE_CHECK).sendToTarget() //뷰 닫기
    }

    fun click_cancel()
    {
        Vars.MainsHandler!!.obtainMessage(Finals.CLOSE_CHECK).sendToTarget()
    }
}