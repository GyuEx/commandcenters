package com.beyondinc.commandcenter.viewmodel

import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterCheck
import com.beyondinc.commandcenter.data.Checkdata
import com.beyondinc.commandcenter.fragment.CheckListFragment
import com.beyondinc.commandcenter.handler.MainThread
import com.beyondinc.commandcenter.util.Vars

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
            items!![i].use = true
        }
        allcheck.postValue(true)
        onCreate()
    }
    fun allDisable(){
        for(i in 0 until items!!.size)
        {
            items!![i].use = false
        }
        allcheck.postValue(false)
        onCreate()
    }

    fun click_Success()
    {

    }

    fun click_cancel()
    {
        Vars.mvm!!.click_check()
    }
}