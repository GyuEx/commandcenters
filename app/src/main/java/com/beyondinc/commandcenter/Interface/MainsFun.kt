package com.beyondinc.commandcenter.Interface

import com.beyondinc.commandcenter.data.Orderdata

interface MainsFun {

    fun setFragment()

    fun showDialogBrief()
    fun showDialogStore()
    fun showDialogRider()
    fun showOrderdetail(code:Int)
    fun showHistory()
    fun showMessage(msg:String,num:String)
    fun showSelect()
    fun showAddress(obj : Any?)
    fun showPayment()
    fun showLoading()

    fun closeDialog()
    fun closeOderdetail()
    fun closeHistory()
    fun closeMessage()
    fun closeSelect()
    fun changeClose()
    fun closeLoading()

    fun detail_Fragment(i : Int)

    fun send_call(tel : String)

    fun re_login()

    fun dispatchTouchEvent()
    fun setting()
    fun exit()
}

