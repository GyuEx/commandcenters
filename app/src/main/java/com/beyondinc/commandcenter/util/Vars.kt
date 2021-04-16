package com.beyondinc.commandcenter.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

object Vars {
    @JvmField
    var mContext: Context? = null
    var mWorkState = 0

    @JvmField
    var isLogin = false

    @JvmField
    var DeviceSize = Point()
}