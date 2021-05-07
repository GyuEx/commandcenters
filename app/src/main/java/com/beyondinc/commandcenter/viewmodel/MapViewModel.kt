package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
import com.beyondinc.commandcenter.net.httpSub
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import java.util.concurrent.ConcurrentHashMap

class MapViewModel : ViewModel()
{
    var mapInstance: NaverMap? = null
    var first: Boolean? = false

    var Item : MutableLiveData<Riderdata> = MutableLiveData()
    var ItemAc : HashMap<Int, Orderdata> = HashMap()
    var ItemPc : HashMap<Int, Orderdata> = HashMap()

    var Dselect : Boolean = false
    var Lselect : Boolean = false

    var Olayer : MutableLiveData<Int> = MutableLiveData()
    var Slayer : MutableLiveData<Int> = MutableLiveData()

    var Drawer : MutableLiveData<Boolean> = MutableLiveData(false)
    var Lrawer : MutableLiveData<Boolean> = MutableLiveData(false)

    var riderTitle : MutableLiveData<String> = MutableLiveData()
    var selectOr : MutableLiveData<Int> = MutableLiveData()
    var selectRi : MutableLiveData<Int> = MutableLiveData()

    var markerList : ArrayList<Marker> = ArrayList()
    var markerPikupList : ArrayList<Marker> = ArrayList()
    var markerAccesList : ArrayList<Marker> = ArrayList()
    var linePikup : ArrayList<PathOverlay> = arrayListOf()
    var lineAcces : ArrayList<PathOverlay> = arrayListOf()

    var passList : ConcurrentHashMap<String, Riderdata> = ConcurrentHashMap()
    var subitemsize : MutableLiveData<Int> = MutableLiveData()

    init
    {
        Log.e("MapViewModel", "MapViewModel init")
        Drawer.postValue(false)
        Lrawer.postValue(false)

        Vars.MapHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("Map View", "" + msg.what)
                if (msg.what == Finals.CREATE_RIDER_MARKER) createRider()
                else if(msg.what == Finals.MAP_FOR_DOPEN) OpenDrawer()
                else if(msg.what == Finals.MAP_FOR_DCLOSE)
                {
                    CloseDrawer()
                    CloseLowLayer()
                }
                else if(msg.what == Finals.MAP_MOVE_FOCUS) MapFocusSet(msg.obj)
                else if(msg.what == Finals.MAP_FOR_REMOVE) CancelRider()
                else if(msg.what == Finals.SELECT_ORDER) selectOr()
                else if(msg.what == Finals.SELECT_EMPTY) emptyOr()
            }
        }
    }

    fun selectOr(){
        if(Lrawer.value!!) selectOr.value = Finals.SELECT_ORDER
    }

    fun emptyOr(){
        selectOr.value = Finals.SELECT_EMPTY
    }

    fun getOr() : Int?{
        return Finals.SELECT_ORDER
    }

    fun click_assign(){
        if(Item.value != null)
        {
            Vars.SubItemHandler!!.obtainMessage(Finals.ORDER_ASSIGN_LIST, Item!!.value!!.id).sendToTarget()
        }
        else
        {
            var toast : Toast = Toast.makeText(Vars.mContext, "선택된 라이더가 없습니다.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,600)
            toast.show()
        }
    }

    fun MapFocusSet(obj: Any){

        Log.e("SET Move" ," Focus Set on ")

        //if(Item.value == obj as Riderdata) return
        Item.value = obj as Riderdata

        removeOrderMaker()

        for (marker in markerList) {
            if (Item!!.value!!.MakerID != marker.icon.id) marker.map = null
        }

        //전체 마커 초기화, 라이더 마커는 다 지웟다 그릴필요없이 선택된것이 아닌것만 삭제함

        val position = LatLng(
            Item?.value!!.latitude!!.toDouble(),
            Item?.value!!.longitude!!.toDouble()
        )
        mapInstance!!.moveCamera(CameraUpdate.scrollTo(position))
        Olayer.postValue(Finals.MAP_FOR_ORDER)
        CloseDrawer()

        if(Item?.value?.workingStateCode == "배정" || Item?.value?.workingStateCode == "픽업")
        {
            Slayer.postValue(Finals.MAP_FOR_STATE)
        }
        riderTitle.postValue("${Item.value!!.name} : 배정 ${Item.value!!.assignCount} / 픽업 ${Item.value!!.pickupCount} / 완료 ${Item.value!!.completeCount}")

        var it : Iterator<String> = Vars.orderList.keys.iterator()
        var pcnt = 0
        var acnt = 0
        var tcnt = 0
        ItemAc.clear()
        ItemPc.clear()

        var tempmap : HashMap<Int, Orderdata> = HashMap()
        while (it.hasNext())
        {
            var itt = it.next()
            if(Vars.orderList[itt]!!.RiderId == Item!!.value!!.id)
            {
                if(Vars.orderList[itt]!!.DeliveryStateName == "배정") {
                    ItemAc[acnt] = Vars.orderList[itt]!!
                    tempmap[tcnt] = Vars.orderList[itt]!!
                    acnt++
                }
                else if (Vars.orderList[itt]!!.DeliveryStateName == "픽업")
                {
                    ItemPc[pcnt] = Vars.orderList[itt]!!
                    tempmap[tcnt] = Vars.orderList[itt]!!
                    pcnt++
                }
                tcnt++
            }
        }

        if(first == false)
        {
            CancelRider()
            first = true
        }
        else
        {
            createAccept()
            createPikup()
            if(tempmap.keys.size > 0) Vars.AssignHandler!!.obtainMessage(Finals.INSERT_ORDER,tempmap).sendToTarget()
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
        Log.e("Called","Called Cancel Rider")

        removeOrderMaker()
        Olayer.postValue(Finals.MAP_FOR_REMOVE)
        Slayer.postValue(Finals.MAP_FOR_REMOVE)
        Item.value = null
        Vars.SubRiderHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        CloseLowLayer()
        createRider()
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
        Lrawer.postValue(false)
        Vars.SubItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        if(Drawer.value == false) Drawer.postValue(true)
        else CloseDrawer()
    }

    fun CloseDrawer(){
        Drawer.postValue(false)
        Dselect = false
        if(!Lselect) Vars.SubItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        Vars.MainsHandler!!.obtainMessage(Finals.CLOSE_KEYBOARD).sendToTarget()
    }

    fun CloseLowLayer(){
        Lrawer.postValue(false)
        Lselect = false
        if(!Dselect) Vars.SubItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
    }

    fun LowLayerClick(){
        Lselect = true
        Dselect = false
        Drawer.postValue(false)
        if(Lrawer.value == false) {
            Lrawer.postValue(true)
            Vars.SubItemHandler!!.obtainMessage(Finals.SELECT_ORDER).sendToTarget()
        }
        else CloseLowLayer()
    }

    fun createRider()
    {
        for (marker in markerList) {
            marker.map = null
        }
        markerList.clear()
        passList.clear()

        var cntj : Int = 0
        var cntu : Int = 0
        var cntd : Int = 0
        var cnts: Int = 0
        var cntt : Int = 0

        var its : Iterator<String> = Vars.riderList?.keys!!.iterator()
        while (its.hasNext())
        {
            var itt = its.next()

            Vars.riderList[itt]?.assignCount = 0
            Vars.riderList[itt]?.pickupCount = 0
            Vars.riderList[itt]?.completeCount = 0
        }

        //라이더 리스트의 모든 카운터를 초기화 하고

        var it : Iterator<String> = Vars.riderList.keys.iterator()
        while (it.hasNext())
        {
            var itk = it.next()

            var rit : Iterator<String> = Vars.orderList.keys.iterator()
            while (rit.hasNext())
            {
                var rtemp = rit.next()
                if ((Vars.orderList[rtemp]!!.DeliveryStateName != "접수" || Vars.orderList[rtemp]!!.DeliveryStateName != "취소") && Vars.orderList[rtemp]!!.RiderId != "0") {
                    if (Vars.riderList.containsKey(Vars.orderList[rtemp]!!.RiderId)) {
                        if (Vars.orderList[rtemp]!!.DeliveryStateName == "배정") Vars.riderList[itk]!!.assignCount += 1
                        else if (Vars.orderList[rtemp]!!.DeliveryStateName == "픽업") Vars.riderList[itk]!!.pickupCount += 1
                        else if (Vars.orderList[rtemp]!!.DeliveryStateName == "완료") Vars.riderList[itk]!!.completeCount += 1
                    }
                }
            }

            val marker = Marker()
            val latitude = Vars.riderList[itk]?.latitude?.toDouble()
            val longitude = Vars.riderList[itk]?.longitude?.toDouble()
            val position = LatLng(latitude!!, longitude!!)

            marker.icon = OverlayImage.fromView(
                RiderMarketView(
                    Vars.mContext!!,
                    Vars.riderList[itk]!!.name!!,
                    Vars.riderList[itk]!!.assignCount!!.toInt(),
                    Vars.riderList[itk]!!.pickupCount!!.toInt()
                )
            )
            Vars.riderList[itk]!!.MakerID = marker.icon.id
            marker.position = position
            marker.setOnClickListener {
                Vars.SubRiderHandler!!.obtainMessage(Finals.MAP_FOR_CALL_RIDER, marker.icon.id).sendToTarget()
                true
            }

            cntj++ // 전체 리스트임
            if ( Vars.riderList[itk]!!.workingStateCode !! == Codes.RIDER_OFF_WORK) cntt++
            else if (Vars.riderList[itk]!!.workingStateCode !! == Codes.RIDER_ON_WORK) cntu++
//                else if (Vars.riderList[itk]!![ritd]!!.workingStateCode !! == "픽업") cntpi++
//                else if (Vars.riderList[itk]!![ritd]!!.workingStateCode !! == "완료") cntco++

            markerList.add(marker)

            if (Vars.f_center.contains(Vars.riderList[itk]!!.centerID) || Vars.riderList[itk]!!.workingStateCode == Codes.RIDER_OFF_WORK)
            {
                continue
            }
            if (latitude > 0 && longitude > 0) {
                marker.map = mapInstance
                passList[itk] = Vars.riderList[itk]!!
            }
        }

        var forstr = "전:${cntj} 운:${cntu} 대:${cntd} 식:${cnts} 퇴:${cntt}"
        Vars.MainsHandler!!.obtainMessage(Finals.INSERT_RIDER_COUNT, forstr).sendToTarget()
        Vars.SubRiderHandler!!.obtainMessage(Finals.INSERT_RIDER, passList).sendToTarget()
    }

    fun createPikup()
    {
        for (marker in markerPikupList) {
            marker.map = null
        }
        markerPikupList.clear()

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
                    markerPikupList.add(customerMarker)

                    val path = PathOverlay()
//                    var task = httpSub()
//                    path.coords = task.execute(agencyPosition,customerPosition).get() // 실경로
                    path.coords = listOf(agencyPosition,customerPosition)
                    path.width = 5
                    path.outlineColor = Vars.mContext!!.getColor(R.color.pickup)
                    path.color = Vars.mContext!!.getColor(R.color.pickup)
                    path.map = mapInstance
                    lineAcces.add(path)
                }
            }
        }
    }

    fun createAccept()
    {
        for (marker in markerAccesList) {
            marker.map = null
        }
        markerAccesList.clear()

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
                    markerAccesList.add(customerMarker)

                    val path = PathOverlay()
                    //var task = httpSub()
                    //path.coords = task.execute(agencyPosition,customerPosition).get()//실경로
                    path.coords = listOf(agencyPosition,customerPosition)//직선경로
                    path.width = 5
                    path.outlineColor = Vars.mContext!!.getColor(R.color.recive)
                    path.color = Vars.mContext!!.getColor(R.color.recive)
                    path.map = mapInstance
                    lineAcces.add(path)
                }
            }
        }
    }

    class RiderMarketView(
        context: Context,
        riderName: String,
        riderAssignCount: Int,
        riderPickupCount: Int
    ) : ConstraintLayout(context) {

        init {
            val view: View = View.inflate(context, R.layout.view_rider_marker, this)
            val backgroundResourceID: Int =
                if (riderAssignCount == 0 && riderPickupCount == 0)
                    R.drawable.ic_marker_idle_rider
                else
                    R.drawable.ic_marker_assigned_rider
            view.setBackgroundResource(backgroundResourceID)
            view.background.alpha = 196

            val nameField: TextView = view.findViewById(R.id.textRiderName)
            val statusField: TextView = view.findViewById(R.id.textRiderStatus)

            nameField.text = riderName
            statusField.text =
                String.format(
                    context.resources.getString(R.string.format_text_rider_status),
                    riderAssignCount, riderPickupCount
                )
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
}