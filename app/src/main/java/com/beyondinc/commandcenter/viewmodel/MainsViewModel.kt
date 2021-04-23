package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.data.Logindata
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

    init {
        Log.e(Tag, "ViewModel Enable Mains")
        layer.postValue(Finals.SELECT_ORDER)
        select.postValue(Finals.SELECT_EMPTY)

        Vars.MainsHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                if(msg.what == Finals.CALL_RIDER) getRiderList()
                else if(msg.what == Finals.INSERT_RIDER) insertRider()
                else if(msg.what == Finals.CALL_ORDER) getOrderList()
            }
        }
        getCenterList()
    }

    fun insertRider()
    {

        Log.e("AAA" , "" + Vars.riderList.size + " // " + Vars.riderList + "\n")

        if(!Logindata.RiderList)
        {
            Vars.MainsHandler!!.obtainMessage(Finals.CALL_ORDER).sendToTarget()
            Logindata.RiderList = true
        }
    }

    fun getCenterList()
    {
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp.put(Procedures.CENTER_LIST,MakeJsonParam().makeCenterListParameter("commandcenter"))
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
        temp.put(Procedures.ORDER_LIST_IN_CENTER, MakeJsonParam().makeFullOrderListParameter("commandcenter",ids))
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
        temp.put(Procedures.RIDER_LIST_IN_CENTER,MakeJsonParam().makeRiderListParameter("commandcenter",ids))
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
        } else {
            select.postValue(Finals.SELECT_STORE)
            showDialog(Finals.storetitle)
        }
    }

    fun click_rider() {
        if (select.value == Finals.SELECT_RIDER) {
            select.postValue(Finals.SELECT_EMPTY)
        } else {
            select.postValue(Finals.SELECT_RIDER)
            showDialog(Finals.ridertitle)
        }
    }

    fun click_map_to_order() {
        if(layer.value != Finals.SELECT_MAP)
        {
            layer.postValue(Finals.SELECT_MAP)
            select.postValue(Finals.SELECT_EMPTY)

        }
        else layer.postValue(Finals.SELECT_ORDER)
    }

    fun click_breifing() {
        showDialog(Finals.breiftitle)
    }

    fun click_check() {
        if (layer.value == Finals.SELECT_ORDER) {
            if (checkview.value == Finals.SELECT_CHECK) {
                checkview.postValue(Finals.SELECT_EMPTY)
            } else {
                checkview.postValue(Finals.SELECT_CHECK)
            }
        }
    }

    fun showDialog(txt : String){
        popuptitle.postValue(txt)
        (Vars.mContext as MainsFun).showDialog()
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