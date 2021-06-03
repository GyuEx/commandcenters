package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.handler.MarkerThread
import com.beyondinc.commandcenter.handler.PlaySoundThread
import com.beyondinc.commandcenter.net.httpSub
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.*
import java.lang.Math.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow

class MapViewModel : ViewModel()
{
    var mapInstance: NaverMap? = null // 네이버맵
    var first: Boolean? = false // 처음인지 여부 최초 로그인시 쓰레드에 의해서 불필요한 요청을 줄일 수 있음

    var Item : MutableLiveData<Riderdata> = MutableLiveData() // 맵에 보여줄 라이더 목록
    var ItemAc : ConcurrentHashMap<Int, Orderdata> = ConcurrentHashMap() // 라이더의 배정 카운터
    var ItemPc : ConcurrentHashMap<Int, Orderdata> = ConcurrentHashMap() // 라이더의 픽업 카운터

    var Dselect : Boolean = false // 좌측 드로워 선택여부
    var Lselect : Boolean = false // 하단 드로워 선택여부

    var Olayer : MutableLiveData<Int> = MutableLiveData() // 라이더 선택시 오더목록 레이어가 열린 상태인지?

    var Drawer : MutableLiveData<Boolean> = MutableLiveData(false) // 좌측 드로워가 열린 상태인지?
    var Lrawer : MutableLiveData<Boolean> = MutableLiveData(false) // 하단 드로워가 열린 상태인지?

    var riderTitle : MutableLiveData<String> = MutableLiveData() // 라이더 상단 표시 텍스트
    var selectOr : MutableLiveData<Int> = MutableLiveData() // 라이더 선택후 표시된 목록을 선택하였는지 여부
    var selectRi : MutableLiveData<Int> = MutableLiveData()

    var markerList: ConcurrentHashMap<Marker,Riderdata> = ConcurrentHashMap() // 라이더 마커 리스트
    var markerPikupList : ArrayList<Marker> = ArrayList() // 픽업마커 리스트
    var markerAccesList : ArrayList<Marker> = ArrayList() // 배정마커 리스트
    var linePikup : ArrayList<PolylineOverlay> = arrayListOf() // 픽업마커 라인 리스트
    var lineAcces : ArrayList<PolylineOverlay> = arrayListOf() // 배정마커 라인 리스트
    var mkPikup : ArrayList<Marker> = arrayListOf()
    var mkAcces : ArrayList<Marker> = arrayListOf()

    var markerAssignAgencyList : HashMap<Orderdata,Marker> = HashMap() // 접수마커 가맹점 리스트
    var markerAssignCustList : HashMap<Orderdata,Marker> = HashMap() // 접수마커 고객 리스트
    var lineAssign : HashMap<Orderdata,PolylineOverlay> = HashMap() //  접수마커 라인 리스트
    var kmAssign : HashMap<Orderdata,Marker> = HashMap()

    var subitemsize : MutableLiveData<Int> = MutableLiveData() // 하단레이어에 보여줄 접수 총갯수

    var imgGray = OverlayImage.fromResource(R.drawable.ic_marker_assigned_rider) // 라이더 마커 이미지
    var imgBlue = OverlayImage.fromResource(R.drawable.ic_marker_idle_rider) //  라이더 마커 이미지
    //오버레이 재활용 안하면 메모리릭이 심하다고함, 네이버지도 개발가이드 발췌

    init
    {
        //Log.e("MapViewModel", "MapViewModel init")
        Drawer.value = false
        Lrawer.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Vars.MapVm = null
    }

    fun dclose(){
        CloseDrawer()
        CloseLowLayer()
        CancelRider()
        mapInstance?.moveCamera(CameraUpdate.zoomTo(13.0))
    }

    fun to_assgin_click(){
        if(Item.value != null)
        {
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.ORDER_ASSIGN_LIST, 0,Item!!.value!!.id).sendToTarget() //아이디는 그냥 주는거임
            removeAssignMaker()
        }
        else
        {
            var toast : Toast = Toast.makeText(Vars.mContext, "선택된 라이더가 없습니다.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,600)
            toast.show()
        }
    }

    fun selectOr(){
        if(Lrawer.value!!) selectOr.value = Finals.SELECT_ORDER
    }

    fun emptyOr(){
        selectOr.value = Finals.SELECT_EMPTY
    }

    fun getOr() : Int?{
        return if(Item.value != null) Finals.SELECT_ORDER else Finals.NOT_USE_CODE
    }

    fun click_assign(){
        var msg = "${Item.value!!.name} 라이더에게 배정"
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SHOW_MESSAGE,0,msg).sendToTarget()
    }

    fun mapFocusing(){

        // 모든 마커의 중심으로 갈수있는 로직을 짜보자

        var MaxY: Double = 0.0
        var MinY: Double = 0.0
        var MaxX: Double = 0.0
        var MinX: Double = 0.0

        if(Item.value != null)
        {
            MaxY = Item.value!!.latitude!!.toDouble()
            MinY = Item.value!!.latitude!!.toDouble()
            MaxX = Item.value!!.longitude!!.toDouble()
            MinX = Item.value!!.longitude!!.toDouble()
        }
        else
        {
            MaxY = markerAssignAgencyList[markerAssignAgencyList.keys.iterator().next()]!!.position.latitude
            MinY = markerAssignAgencyList[markerAssignAgencyList.keys.iterator().next()]!!.position.latitude
            MaxX = markerAssignAgencyList[markerAssignAgencyList.keys.iterator().next()]!!.position.longitude
            MinX = markerAssignAgencyList[markerAssignAgencyList.keys.iterator().next()]!!.position.longitude
        }

        // 기준은 라이더 마커의 중심으로 한다!

        if(markerAccesList.size > 0)
        {
            for(i in 0 until markerAccesList.size)
            {
                if(markerAccesList[i]!!.position.latitude > MaxY) MaxY = markerAccesList[i]!!.position.latitude
                if(markerAccesList[i]!!.position.latitude < MinY) MinY = markerAccesList[i]!!.position.latitude
                if(markerAccesList[i]!!.position.longitude > MaxX) MaxX = markerAccesList[i]!!.position.longitude
                if(markerAccesList[i]!!.position.longitude < MinX) MinX = markerAccesList[i]!!.position.longitude
            }
        }
        if(markerPikupList.size > 0)
        {
            for(i in 0 until markerPikupList.size)
            {
                if(markerPikupList[i]!!.position.latitude > MaxY) MaxY = markerPikupList[i]!!.position.latitude
                if(markerPikupList[i]!!.position.latitude < MinY) MinY = markerPikupList[i]!!.position.latitude
                if(markerPikupList[i]!!.position.longitude > MaxX) MaxX = markerPikupList[i]!!.position.longitude
                if(markerPikupList[i]!!.position.longitude < MinX) MinX = markerPikupList[i]!!.position.longitude
            }
        }

        var ita = markerAssignAgencyList.keys.iterator()
        while (ita.hasNext())
        {
            var itaa = ita.next()
            if(markerAssignAgencyList[itaa]!!.position.latitude > MaxY) MaxY = markerAssignAgencyList[itaa]!!.position.latitude
            if(markerAssignAgencyList[itaa]!!.position.latitude < MinY) MinY = markerAssignAgencyList[itaa]!!.position.latitude
            if(markerAssignAgencyList[itaa]!!.position.longitude > MaxX) MaxX = markerAssignAgencyList[itaa]!!.position.longitude
            if(markerAssignAgencyList[itaa]!!.position.longitude < MinX) MinX = markerAssignAgencyList[itaa]!!.position.longitude
        }
        var itc = markerAssignCustList.keys.iterator()
        while (itc.hasNext())
        {
            var itcc = itc.next()
            if(markerAssignCustList[itcc]!!.position.latitude > MaxY) MaxY = markerAssignCustList[itcc]!!.position.latitude
            if(markerAssignCustList[itcc]!!.position.latitude < MinY) MinY = markerAssignCustList[itcc]!!.position.latitude
            if(markerAssignCustList[itcc]!!.position.longitude > MaxX) MaxX = markerAssignCustList[itcc]!!.position.longitude
            if(markerAssignCustList[itcc]!!.position.longitude < MinX) MinX = markerAssignCustList[itcc]!!.position.longitude
        }

        var LatLng1 = LatLng(MaxY,MaxX)
        var LatLng2 = LatLng(MinY,MinX)

        mapInstance?.moveCamera(CameraUpdate.fitBounds(LatLngBounds(LatLng1,LatLng2)))
        mapInstance?.moveCamera(CameraUpdate.zoomOut()) //간단하게 줌 아웃
    }

    fun MapFocusSet(obj: Riderdata){

        //Log.e("SET Move" ," Focus Set on ")

        var onMove : Boolean = false

        if(Item.value?.id != obj.id) // 같은 라이더는 포커스를 갱신할 필요가 없음
        {
            onMove = true
            Item.value = obj
            CloseDrawer()
            mapFocusing()
            for (marker in markerList.keys) {
                if(Item!!.value!!.MakerID != marker) marker.map = null
                else if (Item!!.value!!.MakerID!!.map == null) marker.map = mapInstance // 내꺼가 없을 경우가 있음..!
            }
            //전체 마커 초기화, 라이더 마커는 다 지웟다 그릴필요없이 선택된것이 아닌것만 삭제함

            Olayer.value = Finals.MAP_FOR_ORDER
            if(Lrawer.value!!) selectOr.value = Finals.SELECT_ORDER
        }

        riderTitle.value = "${Item.value!!.name} : 배정 ${Item.value!!.assignCount} / 픽업 ${Item.value!!.pickupCount} / 완료 ${Item.value!!.completeCount}"

        var it : Iterator<String> = Vars.orderList.keys.iterator()
        var pcnt = 0
        var acnt = 0
        var tcnt = 0
        ItemAc.clear()
        ItemPc.clear()

        var tempmap : ConcurrentHashMap<Int, Orderdata> = ConcurrentHashMap()
        while (it.hasNext())
        {
            var itt = it.next()
            if(Vars.orderList[itt]!!.RiderId == Item!!.value!!.id)
            {
                if(Vars.orderList[itt]!!.DeliveryStateName == "배정") {
                    ItemAc[acnt] = Vars.orderList[itt]!!
                    tempmap[tcnt] = Vars.orderList[itt]!!
                    acnt++
                    tcnt++
                }
                else if (Vars.orderList[itt]!!.DeliveryStateName == "픽업")
                {
                    ItemPc[pcnt] = Vars.orderList[itt]!!
                    tempmap[tcnt] = Vars.orderList[itt]!!
                    pcnt++
                    tcnt++
                }
            }
        }

        if(first == false)
        {
            //Log.e("호출되나?","알려주세요 호출되는지~")
            CancelRider()
            first = true
        }
        else
        {
            createAccept(onMove)
            createPikup(onMove)
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ASSIGN,Finals.INSERT_ORDER,0,tempmap).sendToTarget()
        }
    }

    fun getMapforOrder(): Int? {
        return Finals.MAP_FOR_ORDER
    }
    fun getMapforState(): Int? {
        return Finals.MAP_FOR_STATE
    }

    fun CancelRider()
    {
        if(Item.value == null) return

        removeOrderMaker()
        Olayer.value = Finals.MAP_FOR_REMOVE
        Item.value = null
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBRIDER,Finals.SELECT_EMPTY,0).sendToTarget()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ASSIGN,Finals.SELECT_EMPTY,0).sendToTarget()
        CloseLowLayer()
        mapInstance?.moveCamera(CameraUpdate.zoomTo(13.0))
        MarkerThread().start()
//        for (marker in markerList.keys)
//        {
//            if(marker.map == null) marker.map = mapInstance
//        }
    }

    fun removeOrderMaker()
    {
        for (marker in markerPikupList) {
            marker.map = null
        }
        for (marker in markerAccesList) {
            marker.map = null
        }
        for (marker in lineAcces) {
            marker.map = null
        }
        for (marker in linePikup) {
            marker.map = null
        }
        for (marker in mkPikup) {
            marker.map = null
        }
        for (maker in mkAcces) {
            maker.map = null
        }
    }

    fun removeAssignMaker()
    {
        for (marker in markerAssignAgencyList){
            marker.value.map = null
        }
        markerAssignAgencyList.clear()

        for (marker in markerAssignCustList){
            marker.value.map = null
        }
        markerAssignCustList.clear()

        for (marker in lineAssign){
            marker.value.map = null
        }
        lineAssign.clear()

        for (marker in kmAssign) {
            marker.value.map = null
        }
        kmAssign.clear()
    }

    fun Mapclick(){
        mapInstance?.setOnMapClickListener { point, coord ->
        }

        mapInstance?.setOnMapLongClickListener { point, coord ->
        }

        mapInstance?.setOnMapDoubleTapListener { point, coord ->
            true
        }

        mapInstance?.setOnMapTwoFingerTapListener { point, coord ->
            true
        }
    }

    fun OpenDrawer(){
        Dselect = true
        Lselect = false
        Lrawer.value = false
        removeAssignMaker()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.SELECT_EMPTY,0).sendToTarget()
        if(Drawer.value == false) Drawer.value = true
        else CloseDrawer()
    }

    fun CloseDrawer(){
        Drawer.value = false
        Dselect = false
        if(!Lselect) Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.SELECT_EMPTY,0).sendToTarget()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_KEYBOARD,0).sendToTarget()
    }

    fun CloseLowLayer(){
        Lrawer.value = false
        Lselect = false
        removeAssignMaker()
        if(!Dselect) Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.SELECT_EMPTY,0).sendToTarget()
    }

    fun LowLayerClick(){
        Lselect = true
        Dselect = false
        Drawer.value = false
        if(Lrawer.value == false) {
            Lrawer.value = true
             Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.SELECT_ORDER,0).sendToTarget()
        }
        else CloseLowLayer()
    }

    fun createRider(marker : Riderdata)
    {
        marker.MakerID!!.map = mapInstance
        markerList[marker.MakerID!!] = marker
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBRIDER,Finals.INSERT_RIDER,0,marker).sendToTarget()
    }

    fun updateRider(marker: Riderdata)
    {
        /* 하단부분도 쓰레드에 담을 수 있지만 쓰레드에 담게되면 class 오류가남(지도뷰를 생성하지 않은 쓰레드에서는 마커를 재활용 할 수 없음) */

        if(marker.MakerID!!.map == null && Item.value == null) createRider(marker) //지워졌을경우에 다시 그려줌

        val latitude = marker.latitude!!.toDouble()
        val longitude = marker.longitude!!.toDouble()
        val position = LatLng(latitude!!, longitude!!)
        if(marker!!.assignCount!!.toInt() == 0 && marker!!.pickupCount!!.toInt() == 0) marker.MakerID!!.icon = imgBlue
        else marker.MakerID!!.icon = imgGray
        marker.MakerID!!.position = position
        marker.MakerID!!.captionText = "${marker.name.toString()} \n ${marker.assignCount!!.toInt()} / ${marker.pickupCount!!.toInt()} \n "

        if(Item.value != null)
        {
            riderTitle.value = "${Item.value!!.name} : 배정 ${Item.value!!.assignCount} / 픽업 ${Item.value!!.pickupCount} / 완료 ${Item.value!!.completeCount}"
        }
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBRIDER,Finals.INSERT_RIDER,0,marker).sendToTarget()
    }

    fun removeRider(marker: Riderdata)
    {
        marker.MakerID!!.map = null
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBRIDER,Finals.REMOVE_RIDER_MARKER,0,marker).sendToTarget()
    }

    fun createPikup(move : Boolean)
    {
        for (marker in markerPikupList) {
            marker.map = null
        }
        markerPikupList.clear()

        for (marker in linePikup) {
            marker.map = null
        }
        linePikup.clear()

        for (marker in mkPikup) {
            marker.map = null
        }
        mkPikup.clear()

        var it : Iterator<Int> = ItemPc.keys.iterator()
        while (it.hasNext()) {
            var itk = it.next()
            val agencylatitude = ItemPc[itk]?.AgencyLatitude?.toDouble()
            val agencylongitude = ItemPc[itk]?.AgencyLongitude?.toDouble()
            if (agencylatitude != null && agencylongitude != null) {
                val agencyPosition = LatLng(agencylatitude, agencylongitude)
                val agencyMarker = Marker()
                agencyMarker.icon = OverlayImage.fromView(FixedMarkerView(Vars.mContext!!, true))
                agencyMarker.position = agencyPosition
                agencyMarker.captionText = ItemPc[itk]!!.AgencyName
                agencyMarker.setCaptionAligns(Align.Top)
                agencyMarker.map = mapInstance
                agencyMarker.globalZIndex = 300002
                markerPikupList.add(agencyMarker)

                val customerLatitude = ItemPc[itk]!!.CustomerLatitude?.toDouble()
                val customerLongitude = ItemPc[itk]!!.CustomerLongitude?.toDouble()
                if (customerLatitude != null && customerLongitude != null) {
                    val customerPosition = LatLng(customerLatitude, customerLongitude)
                    val customerMarker = Marker()
                    customerMarker.icon =
                        OverlayImage.fromView(FixedMarkerView(Vars.mContext!!, false))
                    customerMarker.position = customerPosition
                    customerMarker.captionText = ItemPc[itk]!!.CustomerShortAddrNoRoad
                    customerMarker.setCaptionAligns(Align.Top)
                    customerMarker.map = mapInstance
                    customerMarker.globalZIndex = 300002
                    markerPikupList.add(customerMarker)

                    val path = PolylineOverlay()
//                    var task = httpSub()
//                    path.coords = task.execute(agencyPosition,customerPosition).get() // 실경로
                    path.coords = listOf(agencyPosition,customerPosition)
                    path.width = 10
                    path.globalZIndex = 300001
                    path.color = Vars.mContext!!.getColor(R.color.pickup)
                    path.map = mapInstance
                    linePikup.add(path)

                    val mk = Marker()
                    var x = (agencylatitude + customerLatitude) / 2
                    var y = (agencylongitude + customerLongitude) / 2
                    var dist = getDistance(agencyPosition,customerPosition)
                    mk.icon = OverlayImage.fromView(FixedkmView(Vars.mContext!!,dist,Vars.mContext!!.getColor(R.color.pickup)))
                    mk.position = LatLng(x,y)
                    mk.map = mapInstance
                    mk.globalZIndex = 300005
                    mkPikup.add(mk)
                }
            }
        }

        if(move) mapFocusing()
    }

    fun createAccept(move : Boolean)
    {
        for (marker in markerAccesList) {
            marker.map = null
        }
        markerAccesList.clear()

        for (marker in lineAcces) {
            marker.map = null
        }
        lineAcces.clear()

        for (marker in mkAcces) {
            marker.map = null
        }
        mkAcces.clear()

        var it : Iterator<Int> = ItemAc.keys.iterator()
        while (it.hasNext()) {
            var itk = it.next()
            val agencylatitude = ItemAc[itk]?.AgencyLatitude?.toDouble()
            val agencylongitude = ItemAc[itk]?.AgencyLongitude?.toDouble()
            if (agencylatitude != null && agencylongitude != null) {
                val agencyPosition = LatLng(agencylatitude, agencylongitude)
                val agencyMarker = Marker()
                agencyMarker.icon = OverlayImage.fromView(FixedMarkerView(Vars.mContext!!, true))
                agencyMarker.position = agencyPosition
                agencyMarker.captionText = ItemAc[itk]!!.AgencyName
                agencyMarker.setCaptionAligns(Align.Top)
                agencyMarker.map = mapInstance
                agencyMarker.globalZIndex = 300002
                markerAccesList.add(agencyMarker)

                val customerLatitude = ItemAc[itk]!!.CustomerLatitude?.toDouble()
                val customerLongitude = ItemAc[itk]!!.CustomerLongitude?.toDouble()
                if (customerLatitude != null && customerLongitude != null) {
                    val customerPosition = LatLng(customerLatitude, customerLongitude)
                    val customerMarker = Marker()
                    customerMarker.icon =
                        OverlayImage.fromView(FixedMarkerView(Vars.mContext!!, false))
                    customerMarker.position = customerPosition
                    customerMarker.captionText = ItemAc[itk]!!.CustomerShortAddrNoRoad
                    customerMarker.setCaptionAligns(Align.Top)
                    customerMarker.map = mapInstance
                    customerMarker.globalZIndex = 300002
                    markerAccesList.add(customerMarker)

                    val path = PolylineOverlay()
//                    var task = httpSub()
//                    path.coords = task.execute(agencyPosition,customerPosition).get()//실경로
                    path.coords = listOf(agencyPosition,customerPosition)//직선경로
                    path.width = 10
                    path.globalZIndex = 300001
                    path.color = Vars.mContext!!.getColor(R.color.recive)
                    path.map = mapInstance
                    lineAcces.add(path)

                    val mk = Marker()
                    var x = (agencylatitude + customerLatitude) / 2
                    var y = (agencylongitude + customerLongitude) / 2
                    var dist = getDistance(agencyPosition,customerPosition)
                    mk.icon = OverlayImage.fromView(FixedkmView(Vars.mContext!!,dist,Vars.mContext!!.getColor(R.color.recive)))
                    mk.position = LatLng(x,y)
                    mk.globalZIndex = 300005
                    mk.map = mapInstance
                    mkAcces.add(mk)
                }
            }
        }

        if(move) mapFocusing()
    }

    fun createAssign(assorder: Orderdata)
    {
        val agencylatitude = assorder?.AgencyLatitude?.toDouble()
        val agencylongitude = assorder?.AgencyLongitude?.toDouble()
        val customerLatitude = assorder!!.CustomerLatitude?.toDouble()
        val customerLongitude = assorder!!.CustomerLongitude?.toDouble()

        val agencyPosition = LatLng(agencylatitude, agencylongitude)
        val customerPosition = LatLng(customerLatitude, customerLongitude)

        if (agencylatitude != null && agencylongitude != null)
        {
            val agencyMarker = Marker()
            agencyMarker.icon = OverlayImage.fromView(FixedMarkerViewBrief(Vars.mContext!!, true))
            agencyMarker.position = agencyPosition
            agencyMarker.captionText = assorder!!.AgencyName
            agencyMarker.setCaptionAligns(Align.Top)
            agencyMarker.map = mapInstance
            agencyMarker.globalZIndex = 300002
            markerAssignAgencyList[assorder] = agencyMarker

            if (customerLatitude != null && customerLongitude != null)
            {
                val customerMarker = Marker()
                customerMarker.icon =
                        OverlayImage.fromView(FixedMarkerViewBrief(Vars.mContext!!, false))
                customerMarker.position = customerPosition
                customerMarker.captionText = assorder!!.CustomerShortAddrNoRoad
                customerMarker.setCaptionAligns(Align.Top)
                customerMarker.map = mapInstance
                customerMarker.globalZIndex = 300002
                markerAssignCustList[assorder] = (customerMarker)

                val path = PolylineOverlay()
//                var task = httpSub()
//                path.coords = task.execute(agencyPosition,customerPosition).get()//실경로
                path.coords = listOf(agencyPosition,customerPosition)//직선경로
                path.width = 10
                path.globalZIndex = 300001
                path.color = Vars.mContext!!.getColor(R.color.brief)
                path.map = mapInstance
                lineAssign[assorder] = path

                val mk = Marker()
                var x = (agencylatitude + customerLatitude) / 2
                var y = (agencylongitude + customerLongitude) / 2
                var dist = getDistance(agencyPosition,customerPosition)
                mk.icon = OverlayImage.fromView(FixedkmView(Vars.mContext!!,dist,Vars.mContext!!.getColor(R.color.brief)))
                mk.position = LatLng(x,y)
                mk.globalZIndex = 300005
                mk.map = mapInstance
                kmAssign[assorder] = mk

            }
        }
        mapFocusing()
    }

    fun removeAssign(assorder: Orderdata)
    {
        markerAssignAgencyList[assorder]!!.map = null
        markerAssignAgencyList.remove(assorder)
        markerAssignCustList[assorder]!!.map = null
        markerAssignCustList.remove(assorder)
        lineAssign[assorder]!!.map = null
        lineAssign.remove(assorder)
        kmAssign[assorder]!!.map = null
        kmAssign.remove(assorder)
    }

    class FixedMarkerViewBrief(context: Context, isStartPosition: Boolean = false) : ConstraintLayout(context) {
        init {
            val view: View = View.inflate(context, R.layout.view_fixed_marker, this)
            val backgroundResourceID: Int =
                if (isStartPosition) R.drawable.ic_marker_delivery_agency
                else R.drawable.ic_marker_delivery_cust
            view.setBackgroundResource(backgroundResourceID)

            val titleField: TextView = findViewById(R.id.positionName)
            val titleResourceID: Int =
                if (isStartPosition) R.string.text_start
                else R.string.text_arrival
            titleField.setText(titleResourceID)
            titleField.textSize = 12F
        }
    }

    class FixedMarkerView(context: Context, isStartPosition: Boolean = false) : ConstraintLayout(context) {
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

    class FixedkmView(context: Context, km : String, color : Int) : ConstraintLayout(context) {
        init {
            val view: View = View.inflate(context, R.layout.view_km_marker, this)
            val titleField: TextView = findViewById(R.id.positionKm)
            titleField.setTextColor(color)
            titleField.textSize = 17f
            titleField.setTypeface(null, Typeface.BOLD)
            titleField.text = km
        }
    }

    fun getDistance(pos1 : LatLng, pos2 : LatLng): String {

        //오더내용안에 distance가 존재하지만,, 안맞는경우도 많고 null인경우도 많아서 그냥 자체 계산해서 사용
        //단거리 계산에는 오차가 거이 없음

        var lat1 = pos1.latitude
        var lon1 = pos1.longitude
        var lat2 = pos2.latitude
        var lon2 = pos2.longitude

        val R = 6372.8 * 1000 // 계수
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2).pow(2.0) + kotlin.math.sin(dLon / 2).pow(2.0) * kotlin.math.cos(
            toRadians(lat1)
        ) * kotlin.math.cos(toRadians(lat2))
        val c = 2 * kotlin.math.asin(kotlin.math.sqrt(a))
        val r = (R * c).toInt()

        return if(r < 999) r.toString() + "m"
        else (r.toDouble()/1000).toString() + "km"
    }
}