package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars

class MainsViewModel : ViewModel() {
    var Tag = "MainsViewModel"

    var layer = MutableLiveData<Int>()
    var select = MutableLiveData<Int>()
    var popuptitle = MutableLiveData<String>()
    var checkview = MutableLiveData<Int>()

    //View Codes
    val SELECT_EMPTY : Int = 0
    val SELECT_BRIFE : Int = 1
    val SELECT_STORE : Int = 2
    val SELECT_RIDER : Int = 3

    val SELECT_MAP : Int = 14
    val SELECT_ORDER : Int = 15
    val SELECT_CHECK : Int = 16

    val breiftitle = "라이더 배정"
    val storetitle = "가맹점 기준 조회"
    val ridertitle = "라이더 기준 조회"

    init {
        Log.e(Tag, "ViewModel Enable")
        layer.postValue(SELECT_ORDER)
        select.postValue(SELECT_EMPTY)
    }

    fun click_brife() {
        if (select.value == SELECT_BRIFE) {
            select.postValue(SELECT_EMPTY)
        } else {
            select.postValue(SELECT_BRIFE)
        }
    }

    fun click_store() {
        if (select.value == SELECT_STORE) {
            select.postValue(SELECT_EMPTY)
        } else {
            select.postValue(SELECT_STORE)
            showDialog(storetitle)
        }
    }

    fun click_rider() {
        if (select.value == SELECT_RIDER) {
            select.postValue(SELECT_EMPTY)
        } else {
            select.postValue(SELECT_RIDER)
            showDialog(ridertitle)
        }
    }

    fun click_map_to_order() {
        if(layer.value != SELECT_MAP)
        {
            layer.postValue(SELECT_MAP)
            select.postValue(SELECT_EMPTY)

        }
        else layer.postValue(SELECT_ORDER)
    }

    fun click_breifing() {
        showDialog(breiftitle)
    }

    fun click_check(){

        if(checkview.value == SELECT_CHECK)
        {
            checkview.postValue(SELECT_EMPTY)
        }
        else
        {
            checkview.postValue(SELECT_CHECK)
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