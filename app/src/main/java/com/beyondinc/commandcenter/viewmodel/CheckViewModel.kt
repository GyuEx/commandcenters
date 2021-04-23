package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Message
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
        Log.e("CheckView", "CheckView Enable")
        allcheck.postValue(true)

        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterCheck(this)
        }

        Vars.CheckHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("Check View","aaaa")
                if (msg.what == Finals.INSERT_STORE) { insertStore() }
            }
        }
    }

    fun insertStore() {
        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var cnt = 0
        while (it.hasNext())
        {
            var tmp = Vars.centerList[it.next()]
            Log.e("insert", "" + tmp!!.centerName + "\n")
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

//    fun getItems(): ConcurrentHashMap<Int,Checkdata>? {
//        return items
//    }

    fun getId(pos: Int): Int? {
        return items!![pos]?.id
    }

    fun getTitle(pos: Int): String? {
        return items!![pos]?.title
    }

    fun getUse(pos: Int): Boolean? {
        return items!![pos]?.use
    }

    fun setUse(pos: Int){
        items!!.get(pos)?.use = items!!.get(pos)?.use != true
        onCreate()
    }

    fun allEnable(){
        for(i in 0 until items!!.size)
        {
            items!![i]?.use = true
        }
        allcheck.postValue(true)
        onCreate()
    }
    fun allDisable(){
        for(i in 0 until items!!.size)
        {
            items!![i]?.use = false
        }
        allcheck.postValue(false)
        onCreate()
    }

    fun click_Success()
    {

    }

    fun click_cancel()
    {
        //Vars.MainsHandler!!.obtainMessage(Finals.CLOSE_POPUP).sendToTarget()
    }
}