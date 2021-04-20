package com.beyondinc.commandcenter.viewmodel

import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.adapter.RecyclerAdapter
import com.beyondinc.commandcenter.data.Dialogdata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.handler.MainThread
import com.beyondinc.commandcenter.util.Vars

class ItemViewModel : ViewModel() {
    var items: HashMap<Int,Orderdata>? = null
    var adapter: RecyclerAdapter? = null

    var state_brifes = MutableLiveData<Boolean>()
    var state_recive = MutableLiveData<Boolean>()
    var state_pikup = MutableLiveData<Boolean>()
    var state_complete = MutableLiveData<Boolean>()
    var state_cancel = MutableLiveData<Boolean>()

    var count_briefes = MutableLiveData<Int>()
    var count_recive = MutableLiveData<Int>()
    var count_pikup = MutableLiveData<Int>()
    var count_complete = MutableLiveData<Int>()
    var count_cancel = MutableLiveData<Int>()

    init {
        Log.e("Memo", "Memo call")

        //필터 초기값 설정
        state_brifes.postValue(true)
        state_recive.postValue(true)
        state_pikup.postValue(true)
        state_complete.postValue(true)
        state_cancel.postValue(true)

        count_briefes.postValue(0)
        count_recive.postValue(0)
        count_pikup.postValue(0)
        count_complete.postValue(0)
        count_cancel.postValue(0)

        if (items == null) {
            items = HashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapter(this)
        }
        insertLogic()

        var handler: Handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == 1) {

                    if(Vars.oderList?.size < items!!.size)
                    {
                        for(i in Vars.oderList!!.size..items!!.size)
                        {
                            items!!.remove(i)
                        }
                    }
                    Vars.oderList!!.let { items!!.putAll(it) }

                    onCreate()
                }
            }
        }

        val mainthread = MainThread(handler)
        mainthread.start()
    }

    fun insertLogic() {

        for (i in 0..10) {
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
            items!![memo.id] = memo
        }
        onCreate()
    }

    fun ListClick() {
        (Vars.mContext as MainsFun).showOderdetail()
    }

    fun onCreate() {

        var cntbr : Int = 0
        var cntre : Int = 0
        var cntpi : Int = 0
        var cntco : Int = 0
        var cntca : Int = 0

        for(i in 0 until items!!.keys.size)
        {
            if(items!![i]!!.work!! == "접수") cntbr++
            else if (items!![i]!!.work!! == "배정") cntre++
            else if (items!![i]!!.work!! == "픽업") cntpi++
            else if (items!![i]!!.work!! == "완료") cntco++
            else if (items!![i]!!.work!! == "취소") cntca++
        }

        count_briefes.postValue(cntbr)
        count_recive.postValue(cntre)
        count_pikup.postValue(cntpi)
        count_complete.postValue(cntco)
        count_cancel.postValue(cntca)

        adapter!!.notifyDataSetChanged()
    }

    fun onResume() {}

//    fun getItems(flag:Int): HashMap<Int, Orderdata>? {
//        return items
//    }

    fun getUsetime(pos: Int): String? {
        return items!![pos]?.usetime
    }

    fun getResttime(pos: Int): String? {
        return items!![pos]?.resttime
    }

    fun getPay(pos: Int): String? {
        return items!![pos]?.pay
    }

    fun getTitle(pos: Int): String? {
        return items!![pos]?.title
    }

    fun getAdress(pos: Int): String? {
        return items!![pos]?.adress
    }

    fun getPoi(pos: Int): String? {
        return items!![pos]?.poi
    }

    fun getRider(pos: Int): String? {
        return items!![pos]?.rider
    }

    fun getWork(pos: Int): String? {
        return items!![pos]?.work
    }

    fun getPaywon(pos: Int): String? {
        return items!![pos]?.paywon
    }

    fun click_brief_filter(){
        if(state_brifes.value == true)state_brifes.postValue(false)
        else state_brifes.postValue(true)
    }

    fun click_recive_filter(){
        if(state_recive.value == true)state_recive.postValue(false)
        else state_recive.postValue(true)
    }

    fun click_picup_filter(){
        if(state_pikup.value == true)state_pikup.postValue(false)
        else state_pikup.postValue(true)
    }

    fun click_complete_filter(){
        if(state_complete.value == true)state_complete.postValue(false)
        else state_complete.postValue(true)
    }

    fun click_cancel_filter(){
        if(state_cancel.value == true)state_cancel.postValue(false)
        else state_cancel.postValue(true)
    }
}