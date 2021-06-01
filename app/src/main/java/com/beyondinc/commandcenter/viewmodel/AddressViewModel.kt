package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.adapter.RecyclerAdapterAddr
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.repository.database.entity.Addrdata
import com.beyondinc.commandcenter.repository.database.entity.Dongdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AddressViewModel : ViewModel() {

    var itemsdong: MutableLiveData<Array<String>> = MutableLiveData() // 동목록 저장변수
    var itemsAddr: ConcurrentHashMap<Int, Addrdata>? = null // 주소목록 저장 변수
    var adapterAddr: RecyclerAdapterAddr? = null // 주소표시 목록뷰 어뎁터
    var selection : MutableLiveData<String> = MutableLiveData() // 주소검색 "지번,도로명"등 선택 저장 변수
    var searchTxt : MutableLiveData<String> = MutableLiveData() // 검색 텍스트
    var detailtxt : MutableLiveData<String> = MutableLiveData() // 상세주소(기입형) 텍스트
    var hinttxt : MutableLiveData<String> = MutableLiveData() // 상세주소 힌트
    var addr = Addrdata() // 주소선택시 저장클래스
    var from : MutableLiveData<Int> = MutableLiveData() // 0:작업중이아님, 1:검색중, 2:검색완료, 3:검색실패, 4:주소선택, 5:상세주소만변경
    var fromtext : MutableLiveData<String> = MutableLiveData() // 0:작업중이아님, 1:검색중, 2:검색완료, 3:검색실패, 4:주소선택, 5:상세주소만변경
    var foc : MutableLiveData<Boolean> = MutableLiveData() // TEXT VIEW 포커스 여부, False로 변경시 키보드가 자동으로 내려가게 함수 구현되어있음
    var mapInstance: NaverMap? = null // 네이버맵

    var dong : MutableLiveData<String> = MutableLiveData() // 표시되는 동
    var dongcode = "" // 검색시 활용할 동 코드
    var title : MutableLiveData<String> = MutableLiveData() // 표시되는 주소
    var sub : MutableLiveData<String> = MutableLiveData() // 표시되는 도로명 "사용안함"
    var spindefult : MutableLiveData<Int> = MutableLiveData() // 동과 동일한 목록번지를 가져올 스피너의 목록번지
    var item = Orderdata() // 선택된 오더의 저장 클래스
    var first : Boolean = false // 처음인지? 스피너는 처음에 목록번지를 지정하면 0번지가 자동선택되기 때문에 해당 부분 방지용

    val CustMarker = Marker() // 지도 맵 마커

    init {
        Log.e("AddrssView", "CheckView Enable")

        if (itemsAddr == null) {
            itemsAddr = ConcurrentHashMap()
        }
        if (adapterAddr == null) {
            adapterAddr = RecyclerAdapterAddr(this)
        }

        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.INSERT_ADDR,0).sendToTarget()
    }

    override fun onCleared() {
        super.onCleared()
        Vars.AddressVm = null
    }

    fun showMsg(){
        if(from.value == 1 || from.value == 2)
        {
            from.value = 3
            fromtext.value = "조회결과가 존재하지 않습니다."
        }
    }

    fun initfirst(){
        searchTxt.value = ""
        detailtxt.value = ""
        itemsAddr!!.clear()
        foc.value = true
        from.value = 5
        fromtext.value = ""
        onCreateAddr()
    }

    fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        if(!first) {
            for (i in 0 until itemsdong.value!!.size) {
                if (itemsdong.value!![i] == dong.value) {
                    spindefult.value = i
                    dongcode = (Vars.dongList[itemsdong.value!![i]] as Dongdata).code.toString()
                }
            } //스피너의 초기값을 알기위해 번지수를 찾아야됨!

            (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
            (parent.getChildAt(0) as TextView).textSize = 20f

            first = true
        }
        else
        {
            (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
            (parent.getChildAt(0) as TextView).textSize = 20f

            dong.value = itemsdong.value?.get(position)
            dongcode = (Vars.dongList[itemsdong.value?.get(position)] as Dongdata).code.toString()
            searchAddr()
        }
    }

    fun insertdong(orderdata: Orderdata) {

        initfirst()

        item = orderdata

        var si = item.CustomerLongAddr.substring(
            0,
            item.CustomerLongAddr.length - item.CustomerShortAddr.length - 1
        )
        var road = item.CustomerShortAddr.substring(item.CustomerAddrData.length + 1).replace(
            "[",
            ""
        ).replace("]", "")
        dong.value = item.CustomerShortAddrNoRoad.substring(
            0,
            item.CustomerShortAddrNoRoad.indexOf(" ")
        )

        title.value = "${item.CustomerLongAddr}"
        detailtxt.value = item.CustomerDetailAddr

        var shorttmp: SortedMap<String, Any> = Vars.dongList.toSortedMap() // 가나다 순이요~
        //var shorttmp : SortedMap<String, Any> = Vars.dongList.toSortedMap(reverseOrder()) // 역순이요~

        var it: Iterator<String> = shorttmp.keys.iterator()
        var ss = ArrayList<String>()
        while (it.hasNext()) {
            var itt = it.next()
            (shorttmp[itt] as Dongdata).name?.let { it1 -> ss.add(it1) }
        }

        itemsdong.value = ss.toArray(arrayOfNulls(ss.size))

        if(first) {
            for (i in 0 until itemsdong.value!!.size) {
                if (itemsdong.value!![i] == dong.value) {
                    spindefult.value = i
                    dongcode = (Vars.dongList[itemsdong.value!![i]] as Dongdata).code.toString()
                }
            } //스피너의 초기값을 알기위해 번지수를 찾아야됨!
        }

        selection.value = "Road"
        searchTxt.value = road
        searchAddr()
    }

    fun rechoice(){
        if(from.value == 4) {
            from.value = 2
            fromtext.value = "변경하려는 주소를 선택해주세요"
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
            fromtext.value = "변경하려는 주소를 선택해주세요"
            onCreateAddr()
        }
        else
        {
            click_Adress(0)
        }
    }


    fun afterTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
    {
        searchTxt.value = s.toString()
    }

    fun afterTextChanged3(s: CharSequence?, start: Int, before: Int, count: Int)
    {
        detailtxt.value = s.toString()
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
            temp[1] = dongcode
            temp[2] = searchTxt.value.toString()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SEARCH_ADDR,0, temp).sendToTarget()
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
        selection.value = ""
        searchTxt.value = ""
        hinttxt.value = ""
    }

    fun click_finish(){

        addr.Addr = "${addr.CityName} ${addr.CountyName} ${addr.LawTownName} ${addr.Jibun}"
        addr.detailAddress = detailtxt.value.toString()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CHANGE_ADDR,0, addr).sendToTarget()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CHANGE_CLOSE,0).sendToTarget()
    }

    fun closeKeyboard()
    {
        foc.value = false
    }

    fun click_choice(){
        from.value = 4
        title.value = "${addr.CityName} ${addr.CountyName} ${addr.LawTownName} ${addr.Jibun} [${addr.Road}]"

        if(mapInstance != null) makeMarker()
    }

    fun makeMarker()
    {
        if(item.CustomerLatitude != null && item.CustomerLatitude != "")
        {
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
        }
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