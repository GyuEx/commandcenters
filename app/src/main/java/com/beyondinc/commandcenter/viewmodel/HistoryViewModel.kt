package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.adapter.RecyclerAdapter
import com.beyondinc.commandcenter.adapter.RecyclerAdapterHistory
import com.beyondinc.commandcenter.adapter.RecyclerAdapterPopup
import com.beyondinc.commandcenter.data.Dialogdata
import com.beyondinc.commandcenter.data.Historydata
import com.beyondinc.commandcenter.util.Vars
import kotlin.collections.ArrayList

class HistoryViewModel : ViewModel() {
    var items: ArrayList<Historydata>? = null // 리스트에 보여줄 목록
    var adapter: RecyclerAdapterHistory? = null // 리스트 어뎁터

    init {
        //Log.e("Memo", "Memo call")
        if (items == null) {
            items = ArrayList()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterHistory(this)
        }
        insertLogic()
    }

    fun insertLogic() {
        for (i in 0..30) {
            val memo = Historydata()
            memo.id = i
            memo.name = ("$i 번마")
            memo.time = "$i : $i : $i"
            memo.state = "접수"
            memo.code = "000000000" + i
            items!!.add(memo)
        }
        //Log.e("insert", "data inserting")
        adapter!!.notifyDataSetChanged()
    }

    fun onClick(pos: Int){
        //Log.e("PopUp","click event" + items!!.get(pos)!!.name)
    }

    fun close() {

    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun onResume() {}

    fun getItems(): List<Historydata>? {
        return items
    }

    fun getName(pos: Int): String? {
        return items!![pos].name
    }

    fun getTime(pos: Int): String? {
        return items!![pos].time
    }

    fun getState(pos: Int): String? {
        return items!![pos].state
    }

    fun getCode(pos: Int): String? {
        return items!![pos].code
    }
}