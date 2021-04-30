package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterPopup
import com.beyondinc.commandcenter.data.Dialogdata
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class BriefViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int, Dialogdata>? = null
    var adapter: RecyclerAdapterPopup? = null

    init {
        Log.e("Dialogs", "Memo call")
        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterPopup(this)
        }
        insertBrief()
    }

    fun insertBrief() {

        var it : Iterator<String> = Vars.riderList!!.keys.iterator()
        var cnt = 0
        while (it.hasNext())
        {
            var rittemp = it.next()
            val memo = Dialogdata()
            Log.e("aaaaaaaaaaa", "" + Vars.riderList[rittemp]!!.centerID + " // " + Vars.riderList[rittemp]!!.name)
            memo.id = cnt
            memo.name = Vars.riderList[rittemp]!!.name
            memo.velue1 = "1"
            memo.velue2 = "2"
            memo.velue3 = "3"
            items!![cnt] = memo
            cnt++
        }
        onCreate()
    }

    fun onClick(pos: Int){
        Log.e("PopUp","click event" + items!!.get(pos)!!.name)
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun getName(pos: Int): String? {
        return items!![pos]?.name
    }

    fun getVelue1(pos: Int): String? {
        return items!![pos]?.velue1
    }

    fun getVelue2(pos: Int): String? {
        return items!![pos]?.velue2
    }

    fun getVelue3(pos: Int): String? {
        return items!![pos]?.velue3
    }
}