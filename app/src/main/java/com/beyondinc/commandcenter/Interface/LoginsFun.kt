package com.beyondinc.commandcenter.Interface

import android.view.MotionEvent

interface LoginsFun {
    fun Login(id:String,pw:String)
    fun LoginSuccess()
    fun LoginFail()
    fun showLoading()
    fun closeLoading()
    fun downloadApk()
    fun installApk()
}