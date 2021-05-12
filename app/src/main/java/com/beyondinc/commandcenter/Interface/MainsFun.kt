package com.beyondinc.commandcenter.Interface

import android.view.MotionEvent

interface MainsFun {

    fun setFragment()

    fun showDialogBrief()
    fun showDialogStore()
    fun showDialogRider()
    fun showOderdetail()
    fun showHistory()
    fun showMessage(msg:String,num:String)
    fun showSelect()

    fun closeDialog()
    fun closeOderdetail()
    fun closeHistory()
    fun closeMessage()
    fun closeSelect()

    fun detail_Fragment(i : Int)

    fun send_call(tel : String)

    fun dispatchTouchEvent()
    fun setting()
    fun exit()
}

