package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Rect
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import java.util.concurrent.ConcurrentHashMap


class MapViewModel : ViewModel()
{
    var mapInstance: NaverMap? = null
    var first: Boolean? = false

    var Item : MutableLiveData<Riderdata> = MutableLiveData()

    var Dselect : Boolean = false
    var Lselect : Boolean = false

    var Olayer : MutableLiveData<Int> = MutableLiveData()
    var Slayer : MutableLiveData<Int> = MutableLiveData()

    var Drawer : MutableLiveData<Boolean> = MutableLiveData(false)
    var Lrawer : MutableLiveData<Boolean> = MutableLiveData(false)

    var riderTitle : MutableLiveData<String> = MutableLiveData()

    var markerList : ArrayList<Marker> = ArrayList()
    var passList : ConcurrentHashMap<String,Riderdata> = ConcurrentHashMap()

    init
    {
        Drawer.postValue(false)
        Lrawer.postValue(false)

        Log.e("MapViewModel", "MapViewModel init")
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
            }
        }
    }

    fun MapFocusSet(obj: Any){
        Item.value = obj as Riderdata
        val position = LatLng(Item?.value!!.latitude!!.toDouble(), Item?.value!!.longitude!!.toDouble())
        mapInstance!!.moveCamera(CameraUpdate.scrollTo(position))
        Olayer.postValue(Finals.MAP_FOR_ORDER)
        if(Item?.value?.workingStateCode == "배정" || Item?.value?.workingStateCode == "픽업")
        {
            Slayer.postValue(Finals.MAP_FOR_STATE)
        }
        riderTitle.postValue("${Item.value!!.name} : 배정 ${Item.value!!.assignCount} / 픽업 ${Item.value!!.pickupCount} / 완료 ${Item.value!!.assignCount}")
        CloseDrawer()

        if(first == false)
        {
            CancelRider()
            first = true
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
        Olayer.postValue(Finals.MAP_FOR_REMOVE)
        Slayer.postValue(Finals.MAP_FOR_REMOVE)
        Item.value = Riderdata()
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

        var it : Iterator<String> = Vars.riderList.keys.iterator()
        while (it.hasNext())
        {
            var itk = it.next()
            var rit : Iterator<String> = Vars.riderList[itk]!!.keys.iterator()
            while (rit.hasNext())
            {

                val ritd = rit.next()
                val marker = Marker()
                val latitude = Vars.riderList[itk]!![ritd]?.latitude?.toDouble()
                val longitude = Vars.riderList[itk]!![ritd]?.longitude?.toDouble()
                val position = LatLng(latitude!!, longitude!!)

                marker.icon = OverlayImage.fromView(
                    RiderMarketView(
                        Vars.mContext!!,
                        Vars.riderList[itk]!![ritd]!!.name!!,
                        Vars.riderList[itk]!![ritd]!!.assignCount!!.toInt(),
                        Vars.riderList[itk]!![ritd]!!.pickupCount!!.toInt()
                    )
                )
                Vars.riderList[itk]!![ritd]!!.MakerID = marker.icon.id
                marker.position = position
                marker.setOnClickListener {
                    Vars.SubRiderHandler!!.obtainMessage(Finals.MAP_FOR_CALL_RIDER, marker.icon.id).sendToTarget()
                    true
                }

                markerList.add(marker)

                if (Vars.f_center.contains( Vars.riderList[itk]!![ritd]!!.centerID) || Vars.riderList[itk]!![ritd]!!.workingStateCode == Codes.RIDER_OFF_WORK)
                {
                    continue
                }
                if (latitude > 0 && longitude > 0) {
                    marker.map = mapInstance
                    passList[ritd] = Vars.riderList[itk]!![ritd]!!
                }
            }
        }

        var sub : Iterator<String> = passList.keys.iterator()
        var i = 0
        while (sub.hasNext())
        {
            var subi = sub.next()
            if(passList[subi]!!.name!! == Item?.value?.name) i++
        }
        if(i < 1) CancelRider()

        Vars.SubRiderHandler!!.obtainMessage(Finals.INSERT_RIDER,passList).sendToTarget()
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
}