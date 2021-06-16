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
import kotlin.collections.ArrayList

class AgencyViewModel : ViewModel() {
    var items: ArrayList<Agencydata>? = null // 리스트에 보여줄 목록
    var adapter: RecyclerAdapterAgency? = null // 리스트 어뎁터

    init {
        Log.e("Agency", "Init다!!")
        if (items == null) {
            items = ArrayList()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterAgency(this)
        }
        insertLogic()
    }

    fun insertLogic() {
        for (i in 0..30) {
            val memo = Agencydata()
            memo.id = i
            memo.agencyName = ("$i 번마")
            memo.v1 = 1 + i
            memo.v2 = 2 + i
            memo.v3 = 3 + i
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

    fun getItems(): List<Agencydata>? {
        return items
    }

    fun getName(pos: Int): String? {
        return items!![pos].agencyName
    }

    fun getTime(pos: Int): String? {
        return items!![pos].v1.toString()
    }

    fun getState(pos: Int): String? {
        return items!![pos].v2.toString()
    }

    fun getCode(pos: Int): String? {
        return items!![pos].v3.toString()
    }
}