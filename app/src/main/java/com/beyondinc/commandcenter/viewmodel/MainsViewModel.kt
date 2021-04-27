package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.*
import org.json.simple.JSONArray
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainsViewModel : ViewModel() {
    var Tag = "MainsViewModel"

    var layer = MutableLiveData<Int>()
    var select = MutableLiveData<Int>()
    var popuptitle = MutableLiveData<String>()
    var checkview = MutableLiveData<Int>()

    var Item = MutableLiveData(Orderdata())

    init {
        Log.e(Tag, "ViewModel Enable Mains")
        layer.postValue(Finals.SELECT_ORDER)
        select.postValue(Finals.SELECT_EMPTY)

        Vars.MainsHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("Main Hanldler" , "" + msg.what)
                if(msg.what == Finals.CALL_RIDER) getRiderList()
                else if(msg.what == Finals.INSERT_RIDER) insertRider()
                else if(msg.what == Finals.CALL_CENTER) getCenterList()
                else if(msg.what == Finals.CALL_ORDER) getOrderList()
                else if(msg.what == Finals.ClOSE_CHECK) checkview.postValue(Finals.SELECT_EMPTY)
                else if(msg.what == Finals.ORDER_ITEM_SELECT) showOrderDetail(msg.obj)
                else if(msg.what == Finals.HTTP_ERROR) HttpError()
            }
        }
    }

    fun HttpError()
    {
        Toast.makeText(Vars.mContext,"서버접속실패",Toast.LENGTH_SHORT).show()
    }

    fun insertRider()
    {
        if(!Logindata.RiderList)
        {
            Vars.MainsHandler!!.obtainMessage(Finals.CALL_ORDER).sendToTarget()
            Logindata.RiderList = true
        }
    }

    fun getCenterList()
    {
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp.put(Procedures.CENTER_LIST,MakeJsonParam().makeCenterListParameter(Logindata.LoginId!!))
        Vars.sendList.add(temp)
    }

    fun getOrderList()
    {
        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var ids : java.util.ArrayList<String> = java.util.ArrayList()
        while (it.hasNext())
        {
            ids.add(Vars.centerList[it.next()]!!.centerId)
        }
        var temp : HashMap<String, JSONArray> =  HashMap()
        temp.put(Procedures.ORDER_LIST_IN_CENTER, MakeJsonParam().makeFullOrderListParameter(Logindata.LoginId!!,ids))
        Vars.sendList.add(temp)

        getRiderGPS()
    }

    fun getRiderGPS()
    {
        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var ids : java.util.ArrayList<String> = java.util.ArrayList()
        while (it.hasNext())
        {
            ids.add(Vars.centerList[it.next()]!!.centerId)
        }
        var temp : HashMap<String, JSONArray> =  HashMap()
        temp.put(Procedures.RIDER_LOCATION_IN_CENTER, MakeJsonParam().makeRidersLocationParameter(Logindata.LoginId!!,ids))
        Vars.sendList.add(temp)
    }

    fun getRiderList()
    {
        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var ids : ArrayList<String> = ArrayList()
        while (it.hasNext())
        {
            ids.add(Vars.centerList[it.next()]!!.centerId)
        }
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp.put(Procedures.RIDER_LIST_IN_CENTER,MakeJsonParam().makeRiderListParameter(Logindata.LoginId!!,ids))
        Vars.sendList.add(temp)
    }

    fun getSelectMap(): Int? {
        return Finals.SELECT_MAP
    }
    fun getSelectOder(): Int? {
        return Finals.SELECT_ORDER
    }
    fun getSelectCheck(): Int? {
        return Finals.SELECT_CHECK
    }
    fun getSelectRider(): Int? {
        return Finals.SELECT_RIDER
    }
    fun getSelectStore(): Int? {
        return Finals.SELECT_STORE
    }
    fun getSelectBrife(): Int? {
        return Finals.SELECT_BRIFE
    }

    fun MapDrOpen(){
        Vars.MapHandler!!.obtainMessage(Finals.Map_FOR_DOPEN).sendToTarget()
    }

    fun getItemPaymonet() : String?{
        var pay = ""
        if(Item.value!!.ApprovalTypeName == "현금") pay = "(현)"
        else if(Item.value!!.ApprovalTypeName == "카드") pay = "(카)"
        else if(Item.value!!.ApprovalTypeName == "선결제") pay = "(선)"
        pay += " " + Item.value!!.SalesPrice
        return pay
    }
    fun getItemRiderPay() : String?{
        var pay = ""
        if(Item.value!!.ApprovalTypeName == "현금") pay = "(현)"
        else if(Item.value!!.ApprovalTypeName == "카드") pay = "(카)"
        else if(Item.value!!.ApprovalTypeName == "선결제") pay = "(선)"
        pay += " " + Item.value!!.DeliveryFee
        return pay
    }

    fun click_brife() {
        if (select.value == Finals.SELECT_BRIFE) {
            select.postValue(Finals.SELECT_EMPTY)
            Vars.ItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        } else {
            select.postValue(Finals.SELECT_BRIFE)
            Vars.ItemHandler!!.obtainMessage(Finals.SELECT_BRIFE).sendToTarget()
        }
    }

    fun click_store() {
        if (select.value == Finals.SELECT_STORE) {
            select.postValue(Finals.SELECT_EMPTY)
        }
        else if(select.value == Finals.SELECT_BRIFE) {
            Vars.ItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
            select.postValue(Finals.SELECT_STORE)
            showDialog(2)
        }
        else {
            select.postValue(Finals.SELECT_STORE)
            showDialog(2)
        }
    }

    fun click_rider() {
        if (select.value == Finals.SELECT_RIDER) {
            select.postValue(Finals.SELECT_EMPTY)
        }else if(select.value == Finals.SELECT_BRIFE) {
            Vars.ItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
            select.postValue(Finals.SELECT_RIDER)
            showDialog(3)
        }
        else {
            select.postValue(Finals.SELECT_RIDER)
            showDialog(3)
        }
    }

    fun click_map_to_order() {
        if(layer.value != Finals.SELECT_MAP)
        {
            layer.postValue(Finals.SELECT_MAP)
            select.postValue(Finals.SELECT_EMPTY)

        }
        else
        {
            layer.postValue(Finals.SELECT_ORDER)
        }
    }

    fun click_breifing() {
        if(Vars.multiSelectCnt == 0) Toast.makeText(Vars.mContext,"선택된 오더가 없습니다.", Toast.LENGTH_SHORT).show()
        else showDialog(1)
    }

    fun click_check() {
        if (layer.value == Finals.SELECT_ORDER && select.value != Finals.SELECT_BRIFE) {
            if (checkview.value == Finals.SELECT_CHECK) {
                checkview.postValue(Finals.SELECT_EMPTY)
            } else {
                checkview.postValue(Finals.SELECT_CHECK)
            }
        }
    }

    fun showDialog(txt : Int){
        if(txt == 1) {
            popuptitle.postValue(Finals.breiftitle)
            (Vars.mContext as MainsFun).showDialogBrief()
        }
        else if(txt == 2)
        {
            popuptitle.postValue(Finals.storetitle)
            (Vars.mContext as MainsFun).showDialogStore()
        }
        else if(txt == 3)
        {
            popuptitle.postValue(Finals.ridertitle)
            (Vars.mContext as MainsFun).showDialogRider()
        }
    }

    fun showOrderDetail(obj: Any) {
        Item.postValue(obj as Orderdata?)
        (Vars.mContext as MainsFun).showOderdetail()
    }

    fun closeDialog(){
        (Vars.mContext as MainsFun).closeDialog()
    }

    fun closeDetail(){
        (Vars.mContext as MainsFun).closeOderdetail()
    }

    fun closeHistory(){
        (Vars.mContext as MainsFun).closeHistory()
    }

    fun showHistory(){
        (Vars.mContext as MainsFun).showHistory()
    }

    fun showDrawer(){
        (Vars.mContext as MainsFun).showDrawer()
    }

    fun closeDrawer(){
        (Vars.mContext as MainsFun).closeDrawer()
    }
}