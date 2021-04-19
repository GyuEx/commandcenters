package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterCheck
import com.beyondinc.commandcenter.data.Checkdata

class CheckViewModel : ViewModel() {
    var items: ArrayList<Checkdata>? = null
    var adapter: RecyclerAdapterCheck? = null
    var allcheck = MutableLiveData<Boolean>()

    init {
        allcheck.postValue(true)

        Log.e("Memo", "Memo call Check")
        if (items == null) {
            items = ArrayList()
        }
        if (adapter == null) {
            adapter = RecyclerAdapterCheck(this)
        }
        insertLogic()
    }

    fun insertLogic() {
        for (i in 0..6) {
            val memo = Checkdata()
            memo.id = i
            memo.title = i.toString() + "지구"
            memo.use = true
            items!!.add(memo)
        }
        Log.e("insert", "data inserting Check")
        adapter!!.notifyDataSetChanged()
    }


    fun ListClick() {

    }

    fun onCreate() {
        Log.e("insert", "data inserting Check!!!!!!!!!!!!")
        adapter!!.notifyDataSetChanged()
    }

    fun onResume() {}

    fun getItems(): List<Checkdata>? {
        return items
    }

    fun getId(pos: Int): Int? {
        return items!![pos].id
    }

    fun getTitle(pos: Int): String? {
        return items!![pos].title
    }

    fun getUse(pos: Int): Boolean? {
        return items!![pos].use
    }

    fun setUse(pos: Int){
        items!!.get(pos).use = items!!.get(pos).use != true
        onCreate()
    }

    fun allEnable(){
        for(i in 0 until items!!.size)
        {
            val memo = Checkdata()
            memo.id = items!![i].id
            memo.title = items!![i].title
            memo.use = true
            items!!.add(memo)
        }
        allcheck.postValue(true)
        onCreate()
    }
    fun allDisable(){
        for(i in 0 until items!!.size)
        {
            val memo = Checkdata()
            memo.id = items!![i].id
            memo.title = items!![i].title
            memo.use = false
            items!!.add(memo)
        }
        allcheck.postValue(false)
        onCreate()
    }
}