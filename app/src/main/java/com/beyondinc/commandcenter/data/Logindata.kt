package com.beyondinc.commandcenter.data

import android.os.Build
import com.beyondinc.commandcenter.BuildConfig;

object Logindata {

    var isLogin = false

    var LoginId: String? = null
    var LoginPw: String? = null

    var CenterId: String? = null
    var CompanyId: String? = null
    var CompanyName: String? = null
    var CenterName: String? = null
    var UserDesc: String? = null
    var LastLoginDT: String? = null
    var MSG: String? = null
    var SessionExpireMin : Int = 1800

    var CenterList : Boolean = false
    var RiderList : Boolean = false
    var OrderList : Boolean = false

    var appID: String = "7"
    var AppType = "Smart.dim"
    var appVersionName: String = ""
    var deviceModelNumber: String = ""
    var deviceID: String = "b7f0b45550726ee8"
    var devicePhone = "+821222795779" // 앱켜질때 자동으로 갱신함
    var deviceModel = "SM-G950N" // 앱켜질때 자동으로 갱신함
    var devicePhoneNumber: String = ""
    const val appVersion = BuildConfig.VERSION_NAME
}