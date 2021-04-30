package com.beyondinc.commandcenter.Interface

import android.view.MotionEvent

interface MainsFun {

    fun setFragment()

    fun showDialogBrief()
    fun showDialogStore()
    fun showDialogRider()
    fun showOderdetail()
    fun showHistory()

    fun closeDialog()
    fun closeOderdetail()
    fun closeHistory()

    fun dispatchTouchEvent()
    fun setting()
    fun exit()
}

