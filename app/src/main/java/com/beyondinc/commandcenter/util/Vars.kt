package com.beyondinc.commandcenter.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.viewmodel.DialogViewModel
import com.beyondinc.commandcenter.viewmodel.ItemViewModel
import com.beyondinc.commandcenter.viewmodel.MainsViewModel

object Vars {

    var mvm : MainsViewModel ?= null;
    var ivm : ItemViewModel ?= null;

    var mContext: Context? = null
    var mWorkState = 0

    var isLogin = false
    var DeviceSize = Point()

    var oderList : HashMap<Int,Orderdata> = HashMap()
    //var oderList : ArrayList<Orderdata> = ArrayList()
}