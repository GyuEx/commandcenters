package com.beyondinc.commandcenter.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.speech.tts.TextToSpeech
import com.beyondinc.commandcenter.data.Alarmdata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.handler.MarkerThread
import com.beyondinc.commandcenter.net.DACallerInterface
import com.beyondinc.commandcenter.repository.database.entity.Centerdata
import com.beyondinc.commandcenter.repository.database.entity.Dongdata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.naver.maps.map.overlay.Marker
import com.vasone.deliveryalarm.DAClient
import org.json.simple.JSONArray
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@SuppressLint("StaticFieldLeak")
object Vars {

    var mContext: Context? = null
    var sContext: Context? = null // 안쓰고싶었는데 방법이 없네.
    var dContext: Context? = null
    var lContext: Context? = null

    var mLayer : Int = Finals.SELECT_ORDER

    var mWorkState = 0
    var multiSelectCnt = 0
    var LoginResult : String? = null
    var isLoading : Boolean = false

    var timecntGPS = 10 // 라이더 요청주기
    var timecntOT = 60 // 오더갯수 요청주기

    var DeviceSize = Point()

    var sendList: ArrayList<ConcurrentHashMap<String,JSONArray>> = ArrayList()
    var receiveList: ArrayList<ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String, String>>>> = ArrayList()
    var alarmList : LinkedList<Alarmdata> = LinkedList()
    var dongList : ConcurrentHashMap<String,Any> = ConcurrentHashMap()
    var AddrList : ConcurrentHashMap<String,Any> = ConcurrentHashMap()

    var centerList: ConcurrentHashMap<String,Centerdata> = ConcurrentHashMap()
    var centerNick: ConcurrentHashMap<String,String> = ConcurrentHashMap()
    var riderList: ConcurrentHashMap<String,Riderdata> = ConcurrentHashMap()
    var orderList: ConcurrentHashMap<String,Orderdata> = ConcurrentHashMap()
    var centerLastTime: ConcurrentHashMap<String,String> = ConcurrentHashMap()
    var centerOrderCount : ConcurrentHashMap<String,String> = ConcurrentHashMap()

    var MainsHandler : Handler? = null
    var ItemHandler : Handler? = null
    var SubItemHandler : Handler? = null
    var LoginHandler : Handler? = null
    var CheckHandler : Handler? = null
    var MapHandler : Handler? = null
    var SubRiderHandler : Handler? = null
    var AssignHandler : Handler? = null
    var AddressHandler : Handler? = null

    var MainThread : Thread? = null
    var HttpThread : Thread? = null
    var AlarmThread : Thread? = null
    var MarkerThread : Thread? = null
    var CheckThread : Thread? = null

    var daclient : Boolean = false
    var tts : TextToSpeech? = null
    var isForecd : Boolean = false

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
    var FontSize : Int = 30

    /*usefiler */
    var f_center : ArrayList<String> = ArrayList()
    var f_five : ArrayList<String> = ArrayList()

    var f_store : String = ""
    var f_rider : String = ""
}