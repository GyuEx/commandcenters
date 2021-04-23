package com.beyondinc.commandcenter.Interface

interface LoginsFun {
    fun Login(id:String,pw:String)
    fun LoginSuccess()
    fun LoginFail()
}