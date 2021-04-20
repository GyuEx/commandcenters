package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.adapter.RecyclerAdapterPopup
import com.beyondinc.commandcenter.data.Dialogdata
import com.beyondinc.commandcenter.util.Vars
import kotlin.collections.ArrayList

class DialogViewModel : ViewModel() {
    var items: ArrayList<Dialogdata>? = null
    var adapter: RecyclerAdapterPopup? = null

    init {
        Log.e("Memo", "Memo call")
        if (items == null) {
            items = ArrayList()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterPopup(this)
        }
        insertLogic()
    }

    fun insertLogic() {
        for (i in 0..30) {
            val memo = Dialogdata()
            memo.id = i
            memo.name = ("$i 번마")
            memo.velue1 = "" + i
            memo.velue2 = "" + i
            memo.velue3 = "" + i
            items!!.add(memo)
        }
        adapter!!.notifyDataSetChanged()
    }

    fun onClick(pos: Int){
        Log.e("PopUp","click event" + items!!.get(pos)!!.name)
    }

    fun close() {
        (Vars.mContext as MainsFun).closeDialog()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun onResume() {}

    fun getItems(): List<Dialogdata>? {
        return items
    }

    fun getName(pos: Int): String? {
        return items!![pos].name
    }

    fun getVelue1(pos: Int): String? {
        return items!![pos].velue1
    }

    fun getVelue2(pos: Int): String? {
        return items!![pos].velue2
    }

    fun getVelue3(pos: Int): String? {
        return items!![pos].velue3
    }
}