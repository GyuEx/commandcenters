package com.beyondinc.commandcenter.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Handler
import com.beyondinc.commandcenter.data.Alarmdata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.repository.database.entity.Centerdata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import org.json.simple.JSONArray
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@SuppressLint("StaticFieldLeak")
object Vars {

    var mContext: Context? = null
    var sContext: Context? = null // 안쓰고싶었는데 방법이 없네.

    var mWorkState = 0
    var multiSelectCnt = 0
    var LoginResult : String? = null

    var timecnt = 60 // 오더요청주기설정

    var DeviceSize = Point()

    var sendList: ArrayList<HashMap<String,JSONArray>> = ArrayList(Collections.synchronizedList(ArrayList()))
    var receiveList: ArrayList<HashMap<String,ArrayList<HashMap<String, String>>>> = ArrayList(Collections.synchronizedList(ArrayList()))
    var alarmList : LinkedList<Alarmdata> = LinkedList(Collections.synchronizedList(LinkedList()))

    var centerList: ConcurrentHashMap<String,Centerdata> = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
    var riderList: ConcurrentHashMap<String,Riderdata> = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))
    var orderList: ConcurrentHashMap<String,Orderdata> = ConcurrentHashMap(Collections.synchronizedMap(ConcurrentHashMap()))

    var MainsHandler : Handler? = null
    var ItemHandler : Handler? = null
    var SubItemHandler : Handler? = null
    var LoginHandler : Handler? = null
    var CheckHandler : Handler? = null
    var MapHandler : Handler? = null
    var SubRiderHandler : Handler? = null
    var DialogHandler : Handler? = null
    var HistoryHandler : Handler? = null

    var MainThread : Thread? = null
    var HttpThread : Thread? = null
    var AlarmThread : Thread? = null

    /*setting */
    var Usenick : Boolean = false
    var UseTime : Boolean = false
    var UseGana : Boolean = false
    var UseTTS : Boolean = false
    var UseJ : Boolean = false
    var UseB : Boolean = false
    var UseW : Boolean = false
    var UseC : Boolean = false
    var Bright : Int = 10

    /*usefiler */
    var f_center : ArrayList<String> = ArrayList()
    var f_five : ArrayList<String> = ArrayList()

    var f_store : String = ""
    var f_rider : String = ""
}