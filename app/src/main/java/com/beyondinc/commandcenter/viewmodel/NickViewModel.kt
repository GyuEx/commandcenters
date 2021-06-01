package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.SettingFun
import com.beyondinc.commandcenter.adapter.RecyclerAdapterNick
import com.beyondinc.commandcenter.adapter.RecyclerAdapterSub
import com.beyondinc.commandcenter.adapter.RecyclerAdapterSubRider
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.timer

class NickViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int,String>? = null // 보여줄 리스트 목록
    var nicks = MutableLiveData<ConcurrentHashMap<String,String>>() // 보여줄 닉네임 목록
    var adapter: RecyclerAdapterNick? = null // 리스트 어뎁터

    init{
        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapterNick(this)
        }

        insertLogic()
    }

    fun insertLogic(){

        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var cnt = 0
        while(it.hasNext())
        {
            var itt = it.next()
            items!![cnt] = Vars.centerList[itt]!!.centerName
            cnt++
        }

        var itn : Iterator<Int> = items!!.keys.iterator()
        var map = ConcurrentHashMap<String,String>()
        while (itn.hasNext())
        {
            var itnn = itn.next()
            if(Vars.centerNick.containsKey(items!![itnn])) {
                map[items!![itnn]!!] = Vars.centerNick[items!![itnn]]!!
                nicks.value = map
            }
            else
            {
                map[items!![itnn]!!] = ""
                nicks.value = map
            }
        }
        create()
    }

    fun click_cancel() {
        (Vars.sContext as SettingFun).closeDialog()
    }

    fun click_save(){
        var it : Iterator<String> = nicks.value?.keys!!.iterator()
        var pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
        var ed = pref.edit()
        while(it.hasNext())
        {
            var itt = it.next()
            Vars.centerNick[itt] = nicks.value?.get(itt)!!
            ed.putString(itt, nicks.value?.get(itt)!!)
        }
        ed.apply()
        (Vars.sContext as SettingFun).closeDialog()
    }

    fun create() {
        adapter!!.notifyDataSetChanged()
    }

    fun getTitle(pos: Int): String? {
        return items!![pos].toString()
    }

}