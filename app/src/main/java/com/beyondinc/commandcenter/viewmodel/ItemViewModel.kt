package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.adapter.RecyclerAdapter
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.Vars

class ItemViewModel : ViewModel() {
    var items: ArrayList<Orderdata>? = null
    var adapter: RecyclerAdapter? = null

    init {
        Log.e("Memo", "Memo call")
        if (items == null) {
            items = ArrayList()
        }
        if (adapter == null) {
            adapter = RecyclerAdapter(this)
        }
        insertLogic()
    }

    fun insertLogic() {
        for (i in 0..30) {
            val memo = Orderdata()
            memo.id = i
            memo.usetime = i.toString() + "분"
            memo.resttime = "$i 초"
            memo.pay = "카드"
            memo.title = "면곡당 " + i + "호점"
            memo.adress = "장안동 " + i + "번지"
            memo.poi = i.toString() + "편한세상"
            memo.rider = "$i km"
            memo.work = "픽업"
            memo.paywon = "$i 원"
            memo.delay = i
            items!!.add(memo)
        }
        Log.e("insert", "data inserting")
        adapter!!.notifyDataSetChanged()
    }


    fun ListClick() {
        (Vars.mContext as MainsFun).showOderdetail()
    }

    fun onCreate() {
        adapter!!.notifyDataSetChanged()
    }

    fun onResume() {}

    fun getItems(): List<Orderdata>? {
        return items
    }

    fun getUsetime(pos: Int): String? {
        return items!![pos].usetime
    }

    fun getResttime(pos: Int): String? {
        return items!![pos].resttime
    }

    fun getPay(pos: Int): String? {
        return items!![pos].pay
    }

    fun getTitle(pos: Int): String? {
        return items!![pos].title
    }

    fun getAdress(pos: Int): String? {
        return items!![pos].adress
    }

    fun getPoi(pos: Int): String? {
        return items!![pos].poi
    }

    fun getRider(pos: Int): String? {
        return items!![pos].rider
    }

    fun getWork(pos: Int): String? {
        return items!![pos].work
    }

    fun getPaywon(pos: Int): String? {
        return items!![pos].paywon
    }
}