package com.beyondinc.commandcenter.viewmodel

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.adapter.RecyclerAdapterAddr
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.repository.database.entity.Addrdata
import com.beyondinc.commandcenter.repository.database.entity.Agencydata
import com.beyondinc.commandcenter.repository.database.entity.Dongdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AddressViewModel : ViewModel() {

    var statecode : Int = 0 // 0 : 오더지번변경 , 1 : 가맹점신규접수
    var titletext : MutableLiveData<String> = MutableLiveData()
    var btntext : MutableLiveData<String> = MutableLiveData()

    var itemsdong: MutableLiveData<Array<String>> = MutableLiveData() // 동목록 저장변수
    var itemsAddr: ConcurrentHashMap<Int, Addrdata>? = null // 주소목록 저장 변수
    var adapterAddr: RecyclerAdapterAddr? = null // 주소표시 목록뷰 어뎁터
    var selection : MutableLiveData<String> = MutableLiveData() // 주소검색 "지번,도로명"등 선택 저장 변수
    var searchTxt : MutableLiveData<String> = MutableLiveData() // 검색 텍스트
    var detailtxt : MutableLiveData<String> = MutableLiveData() // 상세주소(기입형) 텍스트
    var editfocus : MutableLiveData<String> = MutableLiveData() // 텍스트뷰 포커스
    var hinttxt : MutableLiveData<String> = MutableLiveData() // 상세주소 힌트
    var addr = Addrdata() // 주소선택시 저장클래스
    var from : MutableLiveData<Int> = MutableLiveData() // 0:작업중이아님, 1:검색중, 2:검색완료, 3:검색실패, 4:주소선택, 5:상세주소만변경, 6:접수화면
    var fromtext : MutableLiveData<String> = MutableLiveData() // 0:작업중이아님, 1:검색중, 2:검색완료, 3:검색실패, 4:주소선택, 5:상세주소만변경
    var foc : MutableLiveData<Boolean> = MutableLiveData() // TEXT VIEW 포커스 여부, False로 변경시 키보드가 자동으로 내려가게 함수 구현되어있음
    var mapInstance: NaverMap? = null // 네이버맵

    var dong : MutableLiveData<String> = MutableLiveData() // 표시되는 동
    var dongcode = "" // 검색시 활용할 동 코드
    var title : MutableLiveData<String> = MutableLiveData() // 표시되는 주소
    var sub : MutableLiveData<String> = MutableLiveData() // 표시되는 도로명 "사용안함"
    var spindefult : MutableLiveData<Int> = MutableLiveData() // 동과 동일한 목록번지를 가져올 스피너의 목록번지
    var item_order = Orderdata() // 선택된 오더의 저장 클래스
    var item_agency = Agencydata() // 선택된 가맹점의 저장 클래스
    var first : Boolean = false // 처음인지? 스피너는 처음에 목록번지를 지정하면 0번지가 자동선택되기 때문에 해당 부분 방지용

    // 접수변수
    var orderHistory : MutableLiveData<String> = MutableLiveData()
    var orderSailMoney : MutableLiveData<String> = MutableLiveData()
    var orderarriveTime : MutableLiveData<String> = MutableLiveData()
    var orderTelnumber : MutableLiveData<String> = MutableLiveData()
    var orderNotice : MutableLiveData<String> = MutableLiveData()
    var orderPayment : MutableLiveData<String> = MutableLiveData()

    val CustMarker = Marker() // 지도 맵 마커

    val ArriveTime : MutableLiveData<Array<String>> = MutableLiveData() // 시간목록

    var DeliveryDistance : MutableLiveData<String> = MutableLiveData()
    var DeliveryFee : MutableLiveData<String> = MutableLiveData()

    init
    {
        Log.e("AddrssView", "CheckView Enable")

        if (itemsAddr == null) {
            itemsAddr = ConcurrentHashMap()
        }
        if (adapterAddr == null) {
            adapterAddr = RecyclerAdapterAddr(this)
        }
        selection.value = "Road"
        orderSailMoney.value = ""
        orderarriveTime.value = ""
        editfocus.value = "0"
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("날아간다!","날아갓다!!!!!!!!!!!")
        Vars.AddressVm = null
    }

    fun click_dong(){
        detailtxt.value += "동 "
        if(editfocus.value == "0") editfocus.value = "1" //숫자는 별로 의미없음, 포커스를 글자 끝으로 주기 위함
        else editfocus.value = "0"
    }

    fun click_ho(){
        detailtxt.value += "호 "
        if(editfocus.value == "0") editfocus.value = "1"
        else editfocus.value = "0"
    }

    fun click_cheung(){
        detailtxt.value += "층 "
        if(editfocus.value == "0") editfocus.value = "1"
        else editfocus.value = "0"
    }

    fun click_delete(){
        detailtxt.value = ""
        if(editfocus.value == "0") editfocus.value = "1"
        else editfocus.value = "0"
    }

    fun click_100(){
        if(orderSailMoney.value.isNullOrEmpty()) orderSailMoney.value = "100"
        else orderSailMoney.value = (orderSailMoney.value?.toInt()?.plus(100)).toString()
    }

    fun click_500(){
        if(orderSailMoney.value.isNullOrEmpty()) orderSailMoney.value = "500"
        else orderSailMoney.value = (orderSailMoney.value?.toInt()?.plus(500)).toString()
    }

    fun click_1000(){
        if(orderSailMoney.value.isNullOrEmpty()) orderSailMoney.value = "1000"
        else orderSailMoney.value = (orderSailMoney.value?.toInt()?.plus(1000)).toString()
    }

    fun click_5000(){
        if(orderSailMoney.value.isNullOrEmpty()) orderSailMoney.value = "5000"
        else orderSailMoney.value = (orderSailMoney.value?.toInt()?.plus(5000)).toString()
    }

    fun click_10000(){
        if(orderSailMoney.value.isNullOrEmpty()) orderSailMoney.value = "10000"
        else orderSailMoney.value = (orderSailMoney.value?.toInt()?.plus(10000)).toString()
    }

    fun click_money_delete(){
        orderSailMoney.value = ""
    }

    fun showMsg(){
        if(from.value == 1 || from.value == 2)
        {
            from.value = 3
            fromtext.value = "조회결과가 존재하지 않습니다."
        }
    }

    fun initfirst()
    {
        orderHistory.value = ""
        orderSailMoney.value = ""
        orderarriveTime.value = ""
        orderTelnumber.value = ""
        orderNotice.value = ""
        orderPayment.value = ""

        DeliveryDistance.value = ""
        DeliveryFee.value = ""

        searchTxt.value = ""
        detailtxt.value = ""
        itemsAddr!!.clear()
        foc.value = true
        from.value = 5
        fromtext.value = ""
        onCreateAddr()
    }

    fun onItemSelectedArriveTime(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
         if((parent.getChildAt(0) as TextView).text.toString() != "즉시") {
             var st = (parent.getChildAt(0) as TextView).text.toString().replace("분", "").toInt()
             orderarriveTime.value = st.toString()
         }
    }

    fun onItemSelectedPayment(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        orderPayment.value = (parent.getChildAt(0) as TextView).text.toString()
    }

    fun onItemSelectedBuildName(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if((parent.getChildAt(0) as TextView).text.toString() != "건물명")
        {
            detailtxt.value += (parent.getChildAt(0) as TextView).text.toString() + " "
        }
    }

    fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        Log.e("aaaa","aaaaaaaaaaaaaaaaaaaaa111" + dong.value + " // " + dongcode)
//        if(!first && statecode == 0) {
//            Log.e("aaaa","aaaaaaaaaaaaaaaaaaaaa111" + dong.value)
//            for (i in 0 until itemsdong.value!!.size) {
//                if (itemsdong.value!![i] == dong.value) {
//                    spindefult.value = i
//                    dongcode = (Vars.dongList[itemsdong.value!![i]] as Dongdata).code.toString()
//                }
//            } //스피너의 초기값을 알기위해 번지수를 찾아야됨!
//
//            (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
//            (parent.getChildAt(0) as TextView).textSize = 20f
//
//            first = true
//        }
//        else
//        {
            (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
            (parent.getChildAt(0) as TextView).textSize = 20f
            dong.value = itemsdong.value?.get(position)
            //searchAddr()
//       }
    }

    fun insertdong(initdata: Any?) {

        initfirst()
        itemsdong.value = null

        if(initdata is Orderdata)
        {
            item_order = initdata
            statecode = 0
            titletext.value = "주소변경"
            btntext.value = "완료"

            var road =
                item_order.CustomerShortAddr.substring(item_order.CustomerAddrData.length)
                    .replace(
                        "[",
                        ""
                    ).replace("]", "")
            dong.value = item_order.CustomerShortAddrNoRoad.substring(
                0,
                item_order.CustomerShortAddrNoRoad.indexOf(" ")
            )

            getdonglist()

            searchTxt.value = road

            title.value = "${item_order.CustomerLongAddr}"
            detailtxt.value = item_order.CustomerDetailAddr

            searchAddr()
        }
        else if(initdata is Agencydata)
        {
            item_agency = initdata
            statecode = 1
            titletext.value = "오더접수"
            selection.value = "Jibun"
            hinttxt.value = "지번주소를 입력해주세요."
            from.value = 0

            var time = 0

            if(!item_agency.CallCenterOrderDelay.isNullOrEmpty())
            {
                time = item_agency.CallCenterOrderDelay?.toInt()!!
            }
            if(!item_agency.ArriveAgencyPlanTime.isNullOrEmpty())
            {
                time = item_agency.ArriveAgencyPlanTime?.toInt()!!
            }

            var temptime = ArrayList<String>()
            for(i in time .. 60 step 5)
            {
                if(i == 0) temptime.add("즉시")
                else temptime.add("${i}분")
            }
            ArriveTime.value = temptime.toArray(arrayOfNulls(temptime.size))

            getdonglist()
        }
    }

    fun getdonglist()
    {
        var shorttmp: SortedMap<String, Any> = Vars.dongList.toSortedMap() // 가나다 순이요~
        //var shorttmp : SortedMap<String, Any> = Vars.dongList.toSortedMap(reverseOrder()) // 역순이요~

        var it: Iterator<String> = shorttmp.keys.iterator()
        var ss = ArrayList<String>()
        while (it.hasNext()) {
            var itt = it.next()
            (shorttmp[itt] as Dongdata).name?.let { it1 -> ss.add(it1) }
        }

        itemsdong.value = ss.toArray(arrayOfNulls(ss.size))
    }

    fun rechoice(){
        if(from.value == 4) {
            from.value = 2
            if(statecode == 0) fromtext.value = "변경하려는 주소를 선택해주세요"
            else fromtext.value = "주소를 선택해주세요"
        }
        closeKeyboard()
    }

    fun insertAddr() {

        var it : Iterator<String> = Vars.AddrList.keys.iterator()
        var cnt = 0
        while (it.hasNext())
        {
            var itt = it.next()
            itemsAddr!![cnt] = Vars.AddrList[itt] as Addrdata
            cnt++
        }

        if(from.value != 5)
        {
            from.value = 2
            if(statecode == 0) fromtext.value = "변경하려는 주소를 선택해주세요"
            else fromtext.value = "주소를 선택해주세요"
            onCreateAddr()
        }
        else
        {
            click_Adress(0)
        }
    }

    fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?) : Boolean
    {
        searchAddr()
        val inputMethodManager = Vars.mContext!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v?.windowToken, 0)
        return true
    }

    fun searchAddr()
    {
        closeKeyboard()
        if(searchTxt.value!!.isNotEmpty() && dong.value!!.length > 1)
        {
            itemsAddr!!.clear()
            onCreateAddr()
            if(from.value != 5)
            {
                from.value = 1
                fromtext.value = "검색중"
            }

            var temp : HashMap<Int, String> = HashMap()
            temp[0] = selection.value.toString()
            temp[1] = (Vars.dongList[dong.value!!] as Dongdata).code.toString()
            temp[2] = searchTxt.value.toString()
            if(statecode == 0) Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEARCH_ADDR,0, temp).sendToTarget()
            else Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEARCH_NEW_ADDR,0, temp).sendToTarget()
        }
    }

    fun onCheckedChanged(group : RadioGroup, checkedId : Int)
    {
        if(checkedId == R.id.Road)
        {
            selection.value = "Road"
            hinttxt.value = "도로명주소를 입력해주세요."
        }
        else if(checkedId == R.id.Build)
        {
            selection.value = "Build"
            hinttxt.value = "건물명을 입력해주세요."
        }
        else if(checkedId == R.id.jibun)
        {
            selection.value = "Jibun"
            hinttxt.value = "지번주소를 입력해주세요."
        }

        searchAddr()
    }

    fun onCreateAddr() {
        adapterAddr!!.notifyDataSetChanged()
    }

    fun getTitle(pos: Int): String? {
        return "${itemsAddr!![pos]?.CityName} ${itemsAddr!![pos]?.CountyName} ${itemsAddr!![pos]?.LawTownName} ${itemsAddr!![pos]?.Jibun}"
    }

    fun getSub(pos: Int): String? {
        return "${itemsAddr!![pos]?.CityName} ${itemsAddr!![pos]?.CountyName} ${itemsAddr!![pos]?.Road}"
    }

    fun getPoi(pos: Int): String? {
        return if(itemsAddr!![pos]!!.JibunAddr.length > itemsAddr!![pos]!!.Jibun.length) itemsAddr!![pos]!!.JibunAddr.substring(
            itemsAddr!![pos]!!.Jibun.length + 1
        )
        else ""
    }

    fun click_Adress(pos: Int)
    {
        for(i in 0 until itemsAddr!!.keys.size)
        {
            itemsAddr!![i]!!.use = false
        }
        itemsAddr!![pos]!!.use = itemsAddr!![pos]!!.use != true
        addr = itemsAddr!![pos]!!
        if(itemsAddr!![pos]!!.JibunAddr.length > itemsAddr!![pos]!!.Jibun.length && from.value != 5)
            detailtxt.value = itemsAddr!![pos]!!.JibunAddr.substring(itemsAddr!![pos]!!.Jibun.length + 1)
        else if(from.value != 5) detailtxt.value = ""

        onCreateAddr()
        closeKeyboard()
    }

    fun click_cancel()
    {
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CHANGE_CLOSE,0).sendToTarget()
    }

    fun setMode()
    {
        from.value = 0
        selection.value = "Jibun"
        hinttxt.value = "지번주소를 입력해주세요."
        searchTxt.value = ""
    }

    fun click_finish(){
        if(statecode == 0)
        {
            addr.Addr = "${addr.CityName} ${addr.CountyName} ${addr.LawTownName} ${addr.Jibun}"
            addr.detailAddress = detailtxt.value.toString()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CHANGE_ADDR,0, addr).sendToTarget()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CHANGE_CLOSE,0).sendToTarget()
        }
        else
        {
            if(from.value != 6)
            {
                from.value = 6
                Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.GET_DELIVERY_FEE,0,addr).sendToTarget()
                btntext.value = "저장"
            }
            else if(orderSailMoney.value.isNullOrEmpty() || orderTelnumber.value.isNullOrEmpty())
            {
                Toast.makeText(Vars.mContext,"금액 또는 전화번호가 없습니다.",Toast.LENGTH_LONG).show()
                return
            }
            else
            {
                if(item_agency.DepositYn == "Y")
                {
                    var exeSum = item_agency.DepositWarningPrice!!.toInt()
                    var extSum = item_agency.SumAmt!!.toInt()
                    if(exeSum > extSum)
                    {
                        Toast.makeText(Vars.mContext,"예치금이 ${exeSum}원 미만인 경우에는 주문이 불가능합니다.\n 현재 예치금 잔액 : ${extSum}",Toast.LENGTH_LONG).show()
                        return
                    }
                }
                addr.Addr = "${addr.CityName} ${addr.CountyName} ${addr.LawTownName} ${addr.Jibun}"
                addr.detailAddress = detailtxt.value.toString()
                Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SHOW_MESSAGE,0,"접수").sendToTarget()
            }
        }
    }

    fun insertNewOrder()
    {
        var nHash : HashMap<String,String> = HashMap()
        var approvalCode : String? = ""

        if(orderPayment.value == "현금") approvalCode = "2701"
        else if(orderPayment.value == "카드") approvalCode = "2702"
        else if(orderPayment.value == "선결제") approvalCode = "2704"

        val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val time = Date()
        val timecode = format1.format(time)

        nHash["INPUT_TYPE"] = "I" //등록
        nHash["RECEIPT_ID"] = "" //접수아이디
        nHash["REG_ID_TYPE"] = "1"
        nHash["USER_ID"] = Logindata.LoginId.toString()
        nHash["ORDER_CLASS"] = "102" // 오더구분 : 102 예약
        nHash["DELIVERY_PLAN_DT"] = timecode
        nHash["AGENCY_ID"] = item_agency.AgencyId.toString()
        nHash["CUSTOMER_ID"] = ""
        nHash["CUSTOMERN_AME"] = ""
        nHash["CUSTOMER_PHONE"] = orderTelnumber.value.toString()
        nHash["CUSTOMER_MOBILE"] = ""
        nHash["CUSTOMER_ADDR"] = addr.Addr
        nHash["CUSTOMER_LAWTOWNCODE"] = Vars.dongList[addr.LawTownName].toString()
        nHash["CUSTOMER_LAWTOWNNAME"] = addr.LawTownName
        nHash["CUSTOMER_JIBUN"] = addr.Jibun
        nHash["CUSTOMER_ADDR_DETAIL"] = addr.detailAddress
        nHash["CUSTOMER_BUILDING_MANAGE_NO"] = addr.BuildingManageNo
        nHash["APPROVAL_TYPE"] = approvalCode.toString()
        nHash["PRICE"] = orderSailMoney.value.toString()
        nHash["ORDER_DESC"] = orderHistory.value.toString()
        nHash["CONTENT"] = orderNotice.value.toString()
        nHash["PICKUP_YN"] = "N"
        nHash["CANCEL_RESON"] = ""
        nHash["CUSTOMER_LATITUDE"] = addr.Latitude
        nHash["CUSTOMER_LONGITUDE"] = addr.Longitude
        nHash["ORDER_DELAY"] = orderarriveTime.value.toString()
        nHash["ShortLoadAddress"] = addr.Road

        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.INSERT_NEW_ORDER,0,nHash).sendToTarget()
    }

    fun initDeliFee(data : HashMap<String,String>){
        val df = DecimalFormat("#,###")
        val Distresult = df.format(data["DeliveryDistance"]!!.toLong())
        val Feeresult = df.format(data["DeliveryFee"]!!.toLong())
        DeliveryDistance.value = "배달거리 : ${Distresult} M"
        DeliveryFee.value = "배달금액 : ${Feeresult} 원"
    }

    fun closeKeyboard()
    {
        foc.value = false
    }

    fun click_choice()
    {
        from.value = 4
        title.value = "${addr.CityName} ${addr.CountyName} ${addr.LawTownName} ${addr.Jibun} [${addr.Road}]"
        if(statecode == 1) btntext.value = "다음"
        else btntext.value = "완료"

        if(mapInstance != null) makeMarker()
    }

    fun makeMarker()
    {
 //       if(item_order.CustomerLatitude != null && item_order.CustomerLatitude != "")
 //       {
            val agencylatitude = addr.Latitude?.toDouble()
            val agencylongitude = addr.Longitude?.toDouble()
            if (agencylatitude != null && agencylongitude != null) {
                val agencyPosition = LatLng(agencylatitude, agencylongitude)
                CustMarker.icon = OverlayImage.fromView(
                    FixedMarkerView(
                        Vars.mContext!!,
                        false
                    )
                )
                CustMarker.position = agencyPosition
                CustMarker.captionText = title.value!!
                CustMarker.setCaptionAligns(Align.Top)
                CustMarker.map = mapInstance
                mapInstance!!.moveCamera(CameraUpdate.scrollTo(agencyPosition))
                mapInstance!!.moveCamera(CameraUpdate.zoomTo(16.0)) //확대율 조정
            }
 //       }
    }

    class FixedMarkerView(context: Context, isStartPosition: Boolean = false) : ConstraintLayout(
        context
    ) {
        init {
            val view: View = View.inflate(context, R.layout.view_fixed_marker, this)
            val backgroundResourceID: Int =
                    if (isStartPosition) R.drawable.ic_marker_delivery_start
                    else R.drawable.ic_marker_delivery_dest
            view.setBackgroundResource(backgroundResourceID)

            val titleField: TextView = findViewById(R.id.positionName)
            val titleResourceID: Int =
                    if (isStartPosition) R.string.text_start
                    else R.string.text_arrival
            titleField.setText(titleResourceID)
        }
    }
}