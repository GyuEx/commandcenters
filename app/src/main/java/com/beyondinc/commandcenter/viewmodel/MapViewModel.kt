package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

class MapViewModel : ViewModel()
{
    var mapInstance: NaverMap? = null
    val first: Boolean? = null

    var Olayer : MutableLiveData<Int> = MutableLiveData()
    var Slayer : MutableLiveData<Int> = MutableLiveData()

    var Drawer : MutableLiveData<Boolean> = MutableLiveData(false)
    var Lrawer : MutableLiveData<Boolean> = MutableLiveData(false)

    init {

        Drawer.postValue(false)
        Lrawer.postValue(false)

        Log.e("MapViewModel","MapViewModel init")
        Vars.MapHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("Check View","aaaa")
                if (msg.what == Finals.CREATE_RIDER_MARKER) createRider()
                else if(msg.what == Finals.Map_FOR_DOPEN) OpenDrawer()
            }
        }

        mapInstance?.setOnMapClickListener { point, coord ->
            Log.e("click","Map click")
            Drawer.postValue(false)
            Lrawer.postValue(false)
        }
    }

    fun getMapforOrder(): Int? {
        return Finals.MAP_FOR_ORDER
    }
    fun getMapforState(): Int? {
        return Finals.MAP_FOR_STATE
    }

    fun Mapclick(){
        mapInstance?.setOnMapClickListener { point, coord ->
            Lrawer.postValue(false)
        }
    }

    fun OpenDrawer(){
        if(Drawer.value == false) Drawer.postValue(true)
        else Drawer.postValue(false)
    }

    fun LowLayerClick(){
        Drawer.postValue(false)
        if(Lrawer.value == false) Lrawer.postValue(true)
        else Lrawer.postValue(false)
    }

    fun AllroundClick(){
        Lrawer.postValue(false)
        Drawer.postValue(false)
    }

    fun createRider() {
        var it : Iterator<String> = Vars.riderList.keys.iterator()
        while (it.hasNext())
        {
            var itk = it.next()
            var rit : Iterator<String> = Vars.riderList[itk]!!.keys.iterator()
            while (rit.hasNext())
            {
                val ritd = rit.next()
                if(Vars.riderList[itk]!![ritd]!!.workingStateCode == Codes.RIDER_OFF_WORK)
                {
                    continue
                }
                val latitude = Vars.riderList[itk]!![ritd]?.latitude?.toDouble()
                val longitude = Vars.riderList[itk]!![ritd]?.longitude?.toDouble()
                if (latitude != null && longitude != null) {
                    val marker = Marker()
                    val position = LatLng(latitude, longitude)
                    marker.icon = OverlayImage.fromView(
                        RiderMarketView(
                            Vars.mContext!!,
                            Vars.riderList[itk]!![ritd]!!.name!!,
                            Vars.riderList[itk]!![ritd]!!.assignCount!!.toInt(),
                            Vars.riderList[itk]!![ritd]!!.pickupCount!!.toInt()
                        )
                    )
                    marker.position = position
                    marker.setOnClickListener {
                        mapInstance?.moveCamera(CameraUpdate.scrollTo(marker.position))
                        true
                    }
                    marker.map = mapInstance
                }
            }

            if (first == null) {}
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
}