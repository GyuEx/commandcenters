package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.adapter.RecyclerAdapter
import com.beyondinc.commandcenter.adapter.RecyclerAdapterAgency
import com.beyondinc.commandcenter.adapter.RecyclerAdapterHistory
import com.beyondinc.commandcenter.adapter.RecyclerAdapterPopup
import com.beyondinc.commandcenter.data.Dialogdata
import com.beyondinc.commandcenter.data.Historydata
import com.beyondinc.commandcenter.repository.database.entity.Agencydata
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

class AgencyViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int, Agencydata>? = null // 리스트에 보여줄 목록
    var adapter: RecyclerAdapterAgency? = null // 리스트 어뎁터

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

    fun onClick(pos: Int){
        //Log.e("PopUp","click event" + items!!.get(pos)!!.name)
    }

    fun close() {

    }

    fun clear(){
        items!!.clear()
        adapter!!.notifyDataSetChanged()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
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
        return items!![pos]!!.AgencyName
    }

    fun getLogins(pos: Int): String? {
        return items!![pos]!!.State
    }

    fun getInfo(pos: Int): String? {
        return items!![pos]!!.AgencyName
    }
}