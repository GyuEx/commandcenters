package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.activity.Mains
import com.beyondinc.commandcenter.adapter.RecyclerAdapter
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class ItemViewModel : ViewModel() {
    var Realitems: ConcurrentHashMap<String, Orderdata>? = null // 데이터를 가공할 임시 리스트 (보여주지않음)
    var items: ConcurrentHashMap<Int, Orderdata>? = null // 보여주는 리스트
    var passList: ConcurrentHashMap<Int, Orderdata>? = null // 맵뷰에 라인을 그린 후 저장하는 목록
    var scrolls = MutableLiveData<Int>() // 해당부분에 값을 true로 지정하면 뷰의 스크롤을 최상단으로 올림
    var adapter: RecyclerAdapter? = null // 리스트 어뎁터

    var state_brifes = MutableLiveData<Boolean>() // 접수필터 선택여부
    var state_recive = MutableLiveData<Boolean>() // 배정필터 선택여부
    var state_pikup = MutableLiveData<Boolean>() // 픽업필터 선택여부
    var state_complete = MutableLiveData<Boolean>() // 완료필터 선택여부
    var state_cancel = MutableLiveData<Boolean>() // 취소필터 선택여부

    var count_briefes = MutableLiveData<Int>() // 접수 총 갯수
    var count_recive = MutableLiveData<Int>() // 배정 총 갯수
    var count_pikup = MutableLiveData<Int>() // 픽업 총 갯수
    var count_complete = MutableLiveData<Int>() // 완료 총 갯수
    var count_cancel = MutableLiveData<Int>() // 취소 총 갯수

    var select = MutableLiveData<Int>() // 현재 메인화면에 "배정하기"상태인지?
    var sendedItem : Orderdata? = null // 선택시 보내줄 오더 저장 클래스
    var setAgencyfilter : String = "" // 가맹점 필터 선택시 가맹점명 저장
    var setRiderfilter : String = "" // 라이더 필터 선택시 라이더명 저장

    override fun onCleared() {
        super.onCleared()
        Vars.ItemVm = null
    }

    init {
        Log.e("ItemView", "Memo call")

        //필터 초기값 설정
        state_brifes.value = true
        state_recive.value = true
        state_pikup.value = true
        state_complete.value = true
        state_cancel.value = true

        count_briefes.value = 0
        count_recive.value = 0
        count_pikup.value = 0
        count_complete.value = 0
        count_cancel.value = 0

        if (items == null) {
            items = ConcurrentHashMap()
        }
        if (Realitems == null) {
            Realitems = ConcurrentHashMap()
        }
        if (passList == null) {
            passList = ConcurrentHashMap()
        }
        if (adapter == null) {
            adapter = RecyclerAdapter(this)
        }

        select.value = Finals.SELECT_EMPTY
    }

    fun allClear(){
        Realitems!!.clear()
        items!!.clear()
    }

    fun desableSelect(){
        setAgencyfilter = ""
        setRiderfilter = ""
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
    }
    fun storeSelect(t : String){
        setAgencyfilter = t
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_POPUP,0).sendToTarget()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
    }
    fun riderSelect(t : String){
        setRiderfilter = t
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_POPUP,0).sendToTarget()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
    }

    fun insertLogic()
    {
//        Log.e("/////" , "" + System.currentTimeMillis())

        Vars.orderList!!.let { Realitems!!.putAll(it) }

        ////여기서 필터랑 전체 구현해야댐....///

        var cntbr: Int = 0
        var cntre: Int = 0
        var cntpi: Int = 0
        var cntco: Int = 0
        var cntca: Int = 0

        var it: Iterator<String> = Realitems!!.keys.iterator()
        var cnt = 0
        var itemp: ConcurrentHashMap<Int, Orderdata> = ConcurrentHashMap()
        while (it.hasNext())
        {
            var ctemp = it.next()

            if (select.value == Finals.SELECT_BRIFE) // 배정하기 여부 확인
            {
                if (Realitems!![ctemp]?.DeliveryStateName!! == "접수") cntbr++
                else if (Realitems!![ctemp]?.DeliveryStateName!! == "배정") cntre++
                else if (Realitems!![ctemp]?.DeliveryStateName!! == "픽업") cntpi++
                else if (Realitems!![ctemp]?.DeliveryStateName!! == "완료") cntco++
                else if (Realitems!![ctemp]?.DeliveryStateName!! == "취소") cntca++

                if (Vars.f_center.contains(Realitems!![ctemp]?.RcptCenterId) || Realitems!![ctemp]?.DeliveryStateName != "접수") {
                    continue
                } else {
                    itemp[Realitems!![ctemp]!!.OrderId.toInt()] = Realitems!![ctemp]!!
                }

            }
            else
            {
                if (setAgencyfilter != "") // 가맹점 검색 필터 적용
                {
                    if(setAgencyfilter == Realitems!![ctemp]?.AgencyName)
                    {
                        if (Realitems!![ctemp]?.DeliveryStateName!! == "접수") cntbr++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "배정") cntre++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "픽업") cntpi++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "완료") cntco++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "취소") cntca++
                    }

                    if (setAgencyfilter != Realitems!![ctemp]?.AgencyName) {
                        continue
                    } else {
                        itemp[Realitems!![ctemp]!!.OrderId.toInt()] = Realitems!![ctemp]!!
                    }
                }
                else if (setRiderfilter != "") // 라이더 검색 필터 적용
                {
                    if(setRiderfilter == Realitems!![ctemp]?.RiderName)
                    {
                        if (Realitems!![ctemp]?.DeliveryStateName!! == "접수") cntbr++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "배정") cntre++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "픽업") cntpi++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "완료") cntco++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "취소") cntca++
                    }

                    if (setRiderfilter != Realitems!![ctemp]?.RiderName) {
                        continue
                    } else {
                        itemp[Realitems!![ctemp]!!.OrderId.toInt()] = Realitems!![ctemp]!!
                    }
                }
                else //  필터 없음
                {
                    if(!Vars.f_center.contains(Realitems!![ctemp]?.RcptCenterId))
                    {
                        if (Realitems!![ctemp]?.DeliveryStateName!! == "접수") cntbr++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "배정") cntre++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "픽업") cntpi++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "완료") cntco++
                        else if (Realitems!![ctemp]?.DeliveryStateName!! == "취소") cntca++
                    }

                    if (Vars.f_center.contains(Realitems!![ctemp]?.RcptCenterId) || Vars.f_five.contains(Realitems!![ctemp]?.DeliveryStateName))
                    {
                        continue
                    }
                    else
                    {
                        itemp[Realitems!![ctemp]!!.OrderId.toInt()] = Realitems!![ctemp]!!
                    }
                }
            }
        }

        var shorttmp : SortedMap<Int, Orderdata>
        if(Vars.UseTime) shorttmp = itemp.toSortedMap()
        else shorttmp = itemp.toSortedMap(reverseOrder())

        var finalMap : ConcurrentHashMap<Int, Orderdata> = ConcurrentHashMap()
        var shit : Iterator<Int> = shorttmp.keys.iterator()
        while(shit.hasNext())
        {
            var shitt = shit.next()
            finalMap[cnt] = shorttmp[shitt]!!
            cnt++
        }

        count_briefes.value = cntbr
        count_recive.value = cntre
        count_pikup.value = cntpi
        count_complete.value = cntco
        count_cancel.value = cntca

        var forstr = "배:${cntre} 픽:${cntpi} 완:${cntco}"

        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.INSERT_ORDER_COUNT, 0,forstr).sendToTarget()

        if(finalMap.keys.size < items!!.keys.size)
        {
            for(i in finalMap.keys.size..items!!.keys.size)
            {
                items!!.remove(i)
            }
        } // 아이템의 갯수는 가져온 수치보다 많거나 적을 수 있음

        finalMap!!.let { items!!.putAll(it) }

        onCreate()
//        Log.e("/////" , "" + System.currentTimeMillis()) 계산성능 0.01초정도 소요
    }

    fun ListClick(pos: Int) {
        items!![pos]!!.use = items!![pos]!!.use != true
    }

    fun onCreate() {

        var multi : Int = 0

        if(sendedItem != null)
        {
            sendedItem = Vars.orderList[sendedItem!!.OrderId]
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.ORDER_ITEM_SELECT,0,sendedItem).sendToTarget()
        }

        for(i in 0 until items!!.keys.size)
        {
            if(items!![i]!!.use!! && select.value == Finals.SELECT_BRIFE) multi++
            else if(select.value != Finals.SELECT_BRIFE)
            {
                items!![i]!!.use = false
                multi = 0
            }
        }

        Vars.multiSelectCnt = multi

        adapter!!.notifyDataSetChanged()

        //로그인 프로세스 마지막, 타이머를 켬
        if(!Logindata.OrderList)
        {
            Logindata.OrderList = true
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CONN_ALRAM,0).sendToTarget()
        }
    }

    fun click_Up(){
        scrolls.value =+ 1
    }

    fun getSelectBrife(): Int{
        return Finals.SELECT_BRIFE
    }

    fun getUsetime(pos: Int): String? {
        return if(items!![pos]!!.PackingCompleteYn == "N") items!![pos]?.AgencyRequestTime
        else "포장완료"
    }

    fun getResttime(pos: Int): String? {
        //오더시간을 계산해보자
        var a : String = "0"
        var ft = SimpleDateFormat("HH:mm:ss")

        if(items!![pos]!!.DeliveryStateName == "배정") {
            var now = ft.parse(ft.format(Date()))
            var nt = ft.parse(ft.format(ft.parse(items!![pos]!!.DriverAssignDT)))
            a = ((now.time - nt.time)/60000).toString()
        }
        else if(items!![pos]!!.DeliveryStateName == "접수")
        {
            a = "0"
        }
        else if(items!![pos]!!.DeliveryStateName == "픽업")
        {
            var nt = ft.parse(ft.format(ft.parse(items!![pos]!!.DriverAssignDT)))
            var pt = ft.parse(ft.format(ft.parse(items!![pos]!!.PickupDT)))
            a = ((pt.time - nt.time)/60000).toString()
        }
        return a
    }

    fun getPay(pos: Int): String? {
        return items!![pos]?.ApprovalTypeName
    }

    fun getTitle(pos: Int): String? {
        if(Vars.Usenick) return Vars.centerNick[items!![pos]?.CenterName] + "] " + items!![pos]?.AgencyName
        else return items!![pos]?.CenterName + "] " + items!![pos]?.AgencyName
    }

    fun getAdress(pos: Int): String? {
        return items!![pos]?.CustomerShortAddr
    }

    fun getPoi(pos: Int): String? {
        return items!![pos]?.CustomerDetailAddr
    }

    fun getRider(pos: Int): String? {
        return if(items!![pos]?.DeliveryStateName=="접수") items!![pos]?.DeliveryDistance
        else(items!![pos]?.RiderName)
    }

    fun getWork(pos: Int): String? {
        return items!![pos]?.DeliveryStateName
    }

    fun getPaywon(pos: Int): String? {
        return items!![pos]?.DeliveryFee
    }

    fun setUse(pos: Int){
        //Log.e("UsePos", "" + pos + " // " + items!![pos]!!.use + " // " + select.value)
        if(select.value == Finals.SELECT_BRIFE)
        {
            items!![pos]!!.use = items!![pos]!!.use != true
            onCreate()
        }
        else
        {
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.ORDER_ITEM_SELECT, 0,items?.get(pos)).sendToTarget()
            sendedItem = items?.get(pos)
        }
    }

    fun click_brief_filter(){
        if(state_brifes.value == true)
        {
            state_brifes.value = false
            Vars.f_five.add("접수")
            insertLogic()
        }
        else
        {
            state_brifes.value = true
            Vars.f_five.remove("접수")
            insertLogic()
        }
    }

    fun click_recive_filter(){
        if(state_recive.value == true)
        {
            state_recive.value = false
            Vars.f_five.add("배정")
            insertLogic()
        }
        else
        {
            state_recive.value = true
            Vars.f_five.remove("배정")
            insertLogic()
        }
    }

    fun click_picup_filter(){
        if(state_pikup.value == true)
        {
            state_pikup.value = false
            Vars.f_five.add("픽업")
            insertLogic()
        }
        else
        {
            state_pikup.value = true
            Vars.f_five.remove("픽업")
            insertLogic()
        }
    }

    fun click_complete_filter(){
        if(state_complete.value == true)
        {
            state_complete.value = false
            Vars.f_five.add("완료")
            insertLogic()
        }
        else
        {
            state_complete.value = true
            Vars.f_five.remove("완료")
            insertLogic()
        }
    }

    fun click_cancel_filter(){
        if(state_cancel.value == true)
        {
            state_cancel.value = false
            Vars.f_five.add("취소")
            insertLogic()
        }
        else
        {
            state_cancel.value = true
            Vars.f_five.remove("취소")
            insertLogic()
        }
    }

    fun maincloseDetail(){
        sendedItem = null
    }

    fun getfontsize() : Int?{
        return Vars.FontSize
    }

    fun OrderAssign(s: String){
        if (select.value == Finals.SELECT_EMPTY)
        {
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.ORDER_ASSIGN, 0,s).sendToTarget()
        }
        else if (select.value == Finals.SELECT_BRIFE)
        {
            var makeHash = ConcurrentHashMap<String,ArrayList<String>>()
            var makeArray = ArrayList<String>()
            for(i in 0 until items!!.keys.size)
            {
                if(items!![i]!!.use)
                {
                    makeArray.add(items!![i]!!.OrderId)
                }
            }
            makeHash[s] = makeArray
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.ORDER_ASSIGN_LIST, 0,makeHash).sendToTarget()
        }
    }
}