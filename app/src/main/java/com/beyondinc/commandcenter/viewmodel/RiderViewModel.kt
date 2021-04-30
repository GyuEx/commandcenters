package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterRider
import com.beyondinc.commandcenter.data.Dialogdata
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class RiderViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int, Dialogdata>? = null
    var adapter: RecyclerAdapterRider? = null

    init {
        Log.e("Dialogs", "Memo call")
        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterRider(this)
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

//    fun insertStore() {
//        Vars.riderList!!.let { Realitems!!.putAll(it) }
//
//        var it : Iterator<String> = Realitems!!.keys.iterator()
//        var cnt = 0
//        while (it.hasNext())
//        {
//            var ctemp = Realitems!![it.next()]
//            var rit : Iterator<String> = ctemp!!.keys.iterator()
//            while (rit.hasNext())
//            {
//                var rittemp = rit.next()
//                val memo = Dialogdata()
//                memo.id = cnt
//                memo.name = ctemp[rittemp]!!.name
//                memo.velue1 = "0"
//                memo.velue2 = "0"
//                memo.velue3 = "0"
//                items!![memo.id] = memo
//                cnt++
//            }
//        }
//        onCreate()
//    }
//
//    fun insertRider() {
//        Vars.riderList!!.let { Realitems!!.putAll(it) }
//
//        var it : Iterator<String> = Realitems!!.keys.iterator()
//        var cnt = 0
//        while (it.hasNext())
//        {
//            var ctemp = Realitems!![it.next()]
//            var rit : Iterator<String> = ctemp!!.keys.iterator()
//            while (rit.hasNext())
//            {
//                var rittemp = rit.next()
//                val memo = Dialogdata()
//                memo.id = cnt
//                memo.name = ctemp[rittemp]!!.name
//                memo.velue1 = "0"
//                memo.velue2 = "0"
//                memo.velue3 = "0"
//                items!![memo.id] = memo
//                cnt++
//            }
//        }
//        onCreate()
//    }

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