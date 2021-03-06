package com.beyondinc.commandcenter.Interface

import android.view.MotionEvent

interface LoginsFun {
    fun Login(id:String,pw:String)
    fun LoginSuccess()
    fun LoginFail()
    fun showLoading()
    fun closeLoading()
    fun showDownLoading()
    fun closeDownLoading()
    fun showPassword(pw:String,txt:String)
    fun closePassword()
    fun showMsg()
    fun closeMsg()
}