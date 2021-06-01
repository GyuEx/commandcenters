package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.adapter.RecyclerAdapterSub
import com.beyondinc.commandcenter.adapter.RecyclerAdapterSubRider
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.concurrent.timer

class SubRiderViewModel : ViewModel() {
    var items: ConcurrentHashMap<Int,Riderdata>? = null // 보여줄 아이템 목록
    var adapter: RecyclerAdapterSubRider? = null // 리스트 어뎁터

    var first: Boolean = false // 처음인지 여부, 처음 로그인시 가장 첫 라이더의 좌표로 지도를 이동시키기 위함, 디폴트는 "서울시청"

    var select = MutableLiveData<Int>() // 오더를 선택했는지 여부 복수이므로 불리언이아니라 인트
    var selectedRider : Riderdata? = null // 선택된 라이더

    var searchtxt = MutableLiveData<String>() // 검색주소

    var list = LinkedHashMap<String,Riderdata>() // 해시맵은 순서가 뒤죽박죽이라 갱신시마다 순서가 이상하게 변하므로 링크를 줘서 목록변화엔 지장없게

    init {
        Log.e("SUBRIDER", "Memo call")

        select.value = Finals.SELECT_EMPTY
        searchtxt.value = ""

        if (items == null) {
            items = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
        }
        if (adapter == null) {
            adapter = RecyclerAdapterSubRider(this)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Vars.SubRiderVm = null
    }

    fun selectEmpty()
    {
        select.value = Finals.SELECT_EMPTY
        selectedRider = null
    }

    fun getRider(obj:Any)
    {
//        var Mid = obj
//        var it : Iterator<Int> = items?.keys!!.iterator()
//        while (it.hasNext())
//        {
//            var itt = it.next()
//            if(items!![itt] == Mid)
//            {
                Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.MAP_MOVE_FOCUS, 0,obj as Riderdata).sendToTarget()
                selectedRider = obj
//                break // 그만행
//            }
//        }
    }

    fun refrashrider(){
        if(selectedRider != null) Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.MAP_MOVE_FOCUS,0,selectedRider).sendToTarget()
    }

    fun removeRider(obj : Riderdata){
        list.remove(obj.id)
        onCreate()
    }

    fun afterTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
    {
        searchtxt.value = s.toString()
        onCreate()
    }

    fun insertLogic(obj : Riderdata) {
        list[obj.id!!] = obj
        onCreate()
    }

    fun ListClick(pos: Int) {
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.MAP_MOVE_FOCUS,0, items!![pos]).sendToTarget()
        selectedRider = items!![pos]
    }

    fun clickClose(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.MAP_FOR_DOPEN,0).sendToTarget()
    }

    fun onCreate() {

        var it: Iterator<String> = list.keys.iterator()
        var cnt = 0
        var itemp: ConcurrentHashMap<Int, Riderdata> = ConcurrentHashMap()

        while (it.hasNext()) {
            var ctemp = it.next()
            itemp[list[ctemp]!!.completeCount] = list[ctemp]!!
        }

//        var shorttmp : SortedMap<Int, Riderdata>
//        if(Vars.UseGana) shorttmp = itemp.toSortedMap()
//        else shorttmp = itemp.toSortedMap(reverseOrder())
//
//        var finalMap : ConcurrentHashMap<Int, Riderdata> = ConcurrentHashMap()
//        var shit : Iterator<Int> = shorttmp.keys.iterator()
//
//        while(shit.hasNext())
//        {
//            var shitt = shit.next()
//            if(searchtxt.value!!.isEmpty() || shorttmp[shitt]?.name!!.toLowerCase().contains(searchtxt.value!!))
//            {
//                finalMap[cnt] = shorttmp[shitt]!!
//                cnt++
//            }
//        }

        /// Sort 안함

        var finalMap : ConcurrentHashMap<Int, Riderdata> = ConcurrentHashMap()
        var shit : Iterator<String> = list.keys.iterator()

        while(shit.hasNext())
        {
            var shitt = shit.next()
            if(searchtxt.value!!.isEmpty() || list[shitt]?.name!!.toLowerCase().contains(searchtxt.value!!))
            {
                finalMap[cnt] = list[shitt]!!
                cnt++
            }
        }

        if(finalMap.keys.size < items!!.keys.size)
        {
            for(i in finalMap.keys.size..items!!.keys.size)
            {
                items!!.remove(i)
            }
        }

        finalMap!!.let { items!!.putAll(it) }

        adapter!!.notifyDataSetChanged()

        if(items?.keys?.size!! > 0 && !first)
        {
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.MAP_MOVE_FOCUS, 0,items?.get(0)).sendToTarget()
            first = true
        }
    }

    fun getSelectBrife(): Int{
        return Finals.SELECT_BRIFE
    }

    fun getCnt(pos: Int): String? {
        return "${items!![pos]?.assignCount} / ${items!![pos]?.pickupCount}"
    }

    fun getTitle(pos: Int): String? {
        return items!![pos]?.name
    }

    // 사용할일이 있을걸?
//    fun setUse(pos: Int){
//        Log.e("UsePos","" + pos + " // " + items!![pos]!!.use)
//        items!![pos]!!.use = items!![pos]!!.use != true
//        onCreate()
//    }
}