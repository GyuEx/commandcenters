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
import com.beyondinc.commandcenter.viewmodel.*
import com.naver.maps.map.overlay.Marker
import com.vasone.deliveryalarm.DAClient
import org.json.simple.JSONArray
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@SuppressLint("StaticFieldLeak")
object Vars {

    var mContext: Context? = null // 메인 엑티비티 컨텍스트
    var sContext: Context? = null // 세팅 엑티비티
    var dContext: Context? = null // 디테일프래그먼트(네이버맵쓰려면 컨텍스트를 가져다 줘야함)
    var lContext: Context? = null // 로그인 엑티비티

    var mLayer : Int = Finals.SELECT_ORDER // 메인레이어가 무엇을 보고 있는지 쓰레드가 알 필요가 있음

    var multiSelectCnt = 0 // 아이템뷰 다중배차 선택 갯수

    var timecntGPS = 10 // 라이더 요청주기
    var timecntOT = 60 // 오더갯수 요청주기

    var DeviceSize = Point() // 디바이스 사이즈 (메인 엑티비티 생성시 가져온다, 다이얼로그가 전역으로 사용)

    var sendList: ArrayList<ConcurrentHashMap<String,JSONArray>> = ArrayList() // 서버로 전송할 데이터 큐
    var receiveList: ArrayList<ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String, String>>>> = ArrayList() // 서버에서 전달받은 데이터 큐
    var alarmList : LinkedList<Alarmdata> = LinkedList() // 알람라이브러리에서 전달받은 데이터 큐 (순서를 위해 링크)
    var dongList : ConcurrentHashMap<String,Any> = ConcurrentHashMap() // 주소검색시 "동"리스트
    var AddrList : ConcurrentHashMap<String,Any> = ConcurrentHashMap() // 주소검색시 "주소"리스트

    var centerList: ConcurrentHashMap<String,Centerdata> = ConcurrentHashMap() // 센터리스트
    var centerNick: ConcurrentHashMap<String,String> = ConcurrentHashMap() // 센터닉네임
    var riderList: ConcurrentHashMap<String,Riderdata> = ConcurrentHashMap() // 라이더리스트
    var orderList: ConcurrentHashMap<String,Orderdata> = ConcurrentHashMap() // 오더리스트
    var centerLastTime: ConcurrentHashMap<String,String> = ConcurrentHashMap() // 센터별 마지막 오더타임
    var centerOrderCount : ConcurrentHashMap<String,String> = ConcurrentHashMap() // 센터별 총 오더 갯수

    var ItemVm : ItemViewModel? = null // 메인오더뷰모델
    var LoginVm : LoginViewModel? = null // 로그인뷰모델
    var SubItemVm : SubItemViewModel? = null // 지도오더뷰모델
    var CheckVm : CheckViewModel? = null // 센터선택뷰모델
    var MapVm : MapViewModel? = null // 맵화면 뷰모델
    var SubRiderVm : SubRiderViewModel? = null // 맵화면 라이더목록 뷰모델
    var AssignVm : AssignViewModel? = null // 맵화면 라이더 오더목록 뷰모델
    var AddressVm : AddressViewModel? = null // 주소검색 뷰모델
    var MainVm : MainsViewModel? = null // 메인 뷰모델

    var DataHandler : Handler? = null // 데이터 처리를 전달해줄 프로브

    var MainThread : Thread? = null // 데이터처리를 위한 메인쓰레드
    var HttpThread : Thread? = null // 데이터전송을 위한 HTTP쓰레드
    var AlarmThread : Thread? = null // 알람데이터 처리를 위한 알람 쓰레드
    var MarkerThread : Thread? = null // 지도마커를 생성할 마커 쓰레드
    var CheckThread : Thread? = null // 라이더위치조회, 오더시간갱신등 주기적으로 도는 쓰레드

    var daclient : Boolean = false // 알람 활성화 여부

    var tts : TextToSpeech? = null
    var speechrate = 1.0f // tts 읽기속도 조절
    var speechpitch = 1.0f // tts 톤 조절
    var speechrateinfo = ""
    var speechpitchinfo = ""
    var rateseek = 5
    var toneseek = 5

    /*setting */ //환경설정 변수
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

    /*usefiler */ //필터 변수
    var f_center : ArrayList<String> = ArrayList()
    var f_five : ArrayList<String> = ArrayList()

    var f_store : String = ""
    var f_rider : String = ""
}