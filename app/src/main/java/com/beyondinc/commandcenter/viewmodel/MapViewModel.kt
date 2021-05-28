package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
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
import java.util.concurrent.ConcurrentHashMap

class MapViewModel : ViewModel()
{
    var mapInstance: NaverMap? = null
    var first: Boolean? = false

    var Item : MutableLiveData<Riderdata> = MutableLiveData()
    var ItemAc : ConcurrentHashMap<Int, Orderdata> = ConcurrentHashMap()
    var ItemPc : ConcurrentHashMap<Int, Orderdata> = ConcurrentHashMap()

    var Dselect : Boolean = false
    var Lselect : Boolean = false

    var Olayer : MutableLiveData<Int> = MutableLiveData()

    var Drawer : MutableLiveData<Boolean> = MutableLiveData(false)
    var Lrawer : MutableLiveData<Boolean> = MutableLiveData(false)

    var riderTitle : MutableLiveData<String> = MutableLiveData()
    var selectOr : MutableLiveData<Int> = MutableLiveData()
    var selectRi : MutableLiveData<Int> = MutableLiveData()

    var markerList: ConcurrentHashMap<Marker,Riderdata> = ConcurrentHashMap()
    var markerPikupList : ArrayList<Marker> = ArrayList()
    var markerAccesList : ArrayList<Marker> = ArrayList()
    var linePikup : ArrayList<PolylineOverlay> = arrayListOf()
    var lineAcces : ArrayList<PolylineOverlay> = arrayListOf()

    var markerAssignAgencyList : HashMap<Orderdata,Marker> = HashMap()
    var markerAssignCustList : HashMap<Orderdata,Marker> = HashMap()
    var lineAssign : HashMap<Orderdata,PolylineOverlay> = HashMap()

    var subitemsize : MutableLiveData<Int> = MutableLiveData()

    var imgGray = OverlayImage.fromResource(R.drawable.ic_marker_assigned_rider)
    var imgBlue = OverlayImage.fromResource(R.drawable.ic_marker_idle_rider)
    //오버레이 재활용 안하면 메모리릭이 심하다고함, 네이버지도 개발가이드 발췌

    init
    {
        //Log.e("MapViewModel", "MapViewModel init")
        Drawer.value = false
        Lrawer.value = false

        Vars.MapHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                //Log.e("Map View", "" + msg.what)
                if (msg.what == Finals.CREATE_RIDER_MARKER && msg.obj != null) createRider(msg.obj as Riderdata)
                else if (msg.what == Finals.UPDATE_RIDER_MARKER && msg.obj != null) updateRider(msg.obj as Riderdata)
                else if (msg.what == Finals.REMOVE_RIDER_MARKER && msg.obj != null) removeRider(msg.obj as Riderdata)
                else if(msg.what == Finals.MAP_FOR_DOPEN) OpenDrawer()
                else if(msg.what == Finals.MAP_FOR_DCLOSE) dclose()
                else if(msg.what == Finals.MAP_MOVE_FOCUS) MapFocusSet(msg.obj as Riderdata)
                else if(msg.what == Finals.MAP_FOR_REMOVE) CancelRider()
                else if(msg.what == Finals.SELECT_ORDER) selectOr()
                else if(msg.what == Finals.SELECT_EMPTY) emptyOr()
                else if(msg.what == Finals.INSERT_ORDER_COUNT) subitemsize.value = msg.obj as Int
                else if(msg.what == Finals.MAP_FOR_ASSIGN_CREATE) createAssign(msg.obj as Orderdata)
                else if(msg.what == Finals.MAP_FOR_ASSIGN_REMOVE) removeAssign(msg.obj as Orderdata)
                else if(msg.what == Finals.ORDER_ASSIGN) to_assgin_click()
            }
        }
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
            Vars.SubItemHandler!!.obtainMessage(Finals.ORDER_ASSIGN_LIST, Item!!.value!!.id).sendToTarget() //아이디는 그냥 주는거임
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
        Vars.MainsHandler!!.obtainMessage(Finals.SHOW_MESSAGE,msg).sendToTarget()
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

        if(Item.value?.id != obj.id)
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
            Vars.AssignHandler!!.obtainMessage(Finals.INSERT_ORDER,tempmap).sendToTarget()
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
        Vars.SubRiderHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        Vars.AssignHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        CloseLowLayer()
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
        Vars.SubItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        if(Drawer.value == false) Drawer.value = true
        else CloseDrawer()
    }

    fun CloseDrawer(){
        Drawer.value = false
        Dselect = false
        if(!Lselect) Vars.SubItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        Vars.MainsHandler!!.obtainMessage(Finals.CLOSE_KEYBOARD).sendToTarget()
    }

    fun CloseLowLayer(){
        Lrawer.value = false
        Lselect = false
        removeAssignMaker()
        if(!Dselect) Vars.SubItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
    }

    fun LowLayerClick(){
        Lselect = true
        Dselect = false
        Drawer.value = false
        if(Lrawer.value == false) {
            Lrawer.value = true
             Vars.SubItemHandler!!.obtainMessage(Finals.SELECT_ORDER).sendToTarget()
        }
        else CloseLowLayer()
    }

    fun createRider(marker : Riderdata)
    {
        marker.MakerID!!.map = mapInstance
        markerList[marker.MakerID!!] = marker
        Vars.SubRiderHandler!!.obtainMessage(Finals.INSERT_RIDER,marker).sendToTarget()
    }

    fun updateRider(marker: Riderdata)
    {
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
        Vars.SubRiderHandler!!.obtainMessage(Finals.INSERT_RIDER,marker).sendToTarget()
    }

    fun removeRider(marker: Riderdata)
    {
        marker.MakerID!!.map = null
        Vars.SubRiderHandler!!.obtainMessage(Finals.REMOVE_RIDER_MARKER,marker).sendToTarget()
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
                agencyMarker.globalZIndex = 300000
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
                    customerMarker.globalZIndex = 300000
                    markerPikupList.add(customerMarker)

                    val path = PolylineOverlay()
//                    var task = httpSub()
//                    path.coords = task.execute(agencyPosition,customerPosition).get() // 실경로
                    path.coords = listOf(agencyPosition,customerPosition)
                    path.width = 10
                    path.globalZIndex = 300000
                    path.color = Vars.mContext!!.getColor(R.color.pickup)
                    path.map = mapInstance
                    linePikup.add(path)
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
                agencyMarker.globalZIndex = 300000
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
                    customerMarker.globalZIndex = 300000
                    markerAccesList.add(customerMarker)

                    val path = PolylineOverlay()
//                    var task = httpSub()
//                    path.coords = task.execute(agencyPosition,customerPosition).get()//실경로
                    path.coords = listOf(agencyPosition,customerPosition)//직선경로
                    path.width = 10
                    path.globalZIndex = 300000
                    path.color = Vars.mContext!!.getColor(R.color.recive)
                    path.map = mapInstance
                    lineAcces.add(path)
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
            agencyMarker.globalZIndex = 300000
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
                customerMarker.globalZIndex = 300000
                markerAssignCustList[assorder] = (customerMarker)

                val path = PolylineOverlay()
//                var task = httpSub()
//                path.coords = task.execute(agencyPosition,customerPosition).get()//실경로
                path.coords = listOf(agencyPosition,customerPosition)//직선경로
                path.width = 10
                path.globalZIndex = 300000
                path.color = Vars.mContext!!.getColor(R.color.brief)
                path.map = mapInstance
                lineAssign[assorder] = path
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
}