package com.beyondinc.commandcenter.Interface

import com.beyondinc.commandcenter.data.Orderdata

interface MainsFun {

    fun setFragment()

    fun showDialogBrief()
    fun showDialogStore()
    fun showDialogRider()
    fun showOderdetail()
    fun showHistory()
    fun showMessage(msg:String,num:String)
    fun showSelect()
    fun showAddress(code : Int,item : Orderdata)
    fun showPayment()
    fun showLoading()
    fun showAddrselect()

    fun closeDialog()
    fun closeOderdetail()
    fun closeHistory()
    fun closeMessage()
    fun closeSelect()
    fun changeClose()
    fun closeLoading()
    fun closeAddrselect()

    fun detail_Fragment(i : Int)

    fun send_call(tel : String)

    fun dispatchTouchEvent()
    fun setting()
    fun exit()
}

