package com.beyondinc.commandcenter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.util.Vars

class MainsViewModel : ViewModel() {
    @JvmField
    var layer = MutableLiveData<Int>()
    var Tag = "MainsViewModel"

    init {
        Log.e(Tag, "ViewModel Enable")
        layer.postValue(0)
    }

    fun setLayoutBtn1() {
        if (layer.value == 1) {
            layer.postValue(0)
        } else {
            layer.postValue(1)
        }
    }

    fun setLayoutBtn2() {
        layer.postValue(2)
    }

    fun setLayoutBtn3() {
        layer.postValue(3)
    }

    fun showDialog(){
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
}