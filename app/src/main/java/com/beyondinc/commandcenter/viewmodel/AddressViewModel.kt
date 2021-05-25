package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.adapter.RecyclerAdapterAddr
import com.beyondinc.commandcenter.adapter.RecyclerAdapterDong
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
import org.w3c.dom.Text
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap


class AddressViewModel : ViewModel() {

    var itemsdong: ConcurrentHashMap<Int, Dongdata>? = null
    var itemsAddr: ConcurrentHashMap<Int, Addrdata>? = null
    var adapterdong: RecyclerAdapterDong? = null
    var adapterAddr: RecyclerAdapterAddr? = null
    var selection : MutableLiveData<String> = MutableLiveData()
    var searchTxt = ""
    var detailTxt : MutableLiveData<String> = MutableLiveData()
    var dong = ""
    var addr = Addrdata()
    var from : MutableLiveData<Int> = MutableLiveData() // 0:기본 1:트랜스폼 2: 상세온리
    var foc : MutableLiveData<Boolean> = MutableLiveData()
    var mapInstance: NaverMap? = null

    var title : MutableLiveData<String> = MutableLiveData()
    var sub : MutableLiveData<String> = MutableLiveData()
    var poi : MutableLiveData<String> = MutableLiveData()
    var item = Orderdata()

    val CustMarker = Marker()

    init {
        Log.e("AddrssView", "CheckView Enable")

        selection.value = "Jibun"

        if (itemsdong == null) {
            itemsdong = ConcurrentHashMap()
        }
        if (adapterdong == null) {
            adapterdong = RecyclerAdapterDong(this)
        }

        if (itemsAddr == null) {
            itemsAddr = ConcurrentHashMap()
        }
        if (adapterAddr == null) {
            adapterAddr = RecyclerAdapterAddr(this)
        }

        Vars.AddressHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == Finals.INSERT_ADDR) insertdong()
                else if(msg.what == Finals.SEARCH_ADDR) insertAddr()
            }
        }
        insertdong()
    }

    fun initfirst(){
        searchTxt = ""
        detailTxt.value = ""
        itemsAddr!!.clear()
        foc.value = true
        onCreateAddr()
    }

    fun iteminit(init : Orderdata){
        item = init
    }

    fun insertdong() {

            initfirst()
            itemsdong!!.clear()

            var shorttmp: SortedMap<String, Any> = Vars.dongList.toSortedMap() // 가나다 순이요~
            //var shorttmp : SortedMap<String, Any> = Vars.dongList.toSortedMap(reverseOrder()) // 역순이요~

            var it: Iterator<String> = shorttmp.keys.iterator()
            var cnt = 0
            while (it.hasNext()) {
                var itt = it.next()
                itemsdong!![cnt] = shorttmp[itt] as Dongdata
                cnt++
            }
            onCreateDong()
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
        onCreateAddr()

        if(from.value == 2)
        {
            click_Adress(0)
            if(mapInstance != null) makeMarker()
        }
    }


    fun afterTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
    {
        searchTxt = s.toString()
    }

    fun afterTextChanged2(s: CharSequence?, start: Int, before: Int, count: Int)
    {
        detailTxt.value = s.toString()
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
        itemsAddr!!.clear()
        onCreateAddr()

        if(searchTxt.isNotEmpty() && dong.length > 1)
        {
            var temp : HashMap<Int, String> = HashMap()
            temp[0] = selection.value.toString()
            temp[1] = dong
            temp[2] = searchTxt
            Vars.MainsHandler!!.obtainMessage(Finals.SEARCH_ADDR, temp).sendToTarget()
        }
    }

    fun selClick() {
        if(selection.value == "Jibun") selection.value = "Road"
        else if(selection.value == "Road") selection.value = "Build"
        else if(selection.value == "Build") selection.value = "Jibun"
        searchAddr()
        closeKeyboard()
    }

    fun onCreateDong() {
        adapterdong!!.notifyDataSetChanged()
    }

    fun onCreateAddr() {
        adapterAddr!!.notifyDataSetChanged()
    }

    fun getDong(pos: Int): String? {
        return itemsdong!![pos]!!.name
    }

    fun getTitle(pos: Int): String? {
        return "${itemsAddr!![pos]?.CityName} ${itemsAddr!![pos]?.CountyName} ${itemsAddr!![pos]?.LawTownName} ${itemsAddr!![pos]?.Jibun}"
    }

    fun getSub(pos: Int): String? {
        return "${itemsAddr!![pos]?.CityName} ${itemsAddr!![pos]?.CountyName} ${itemsAddr!![pos]?.Road}"
    }

    fun getPoi(pos: Int): String? {
        return if(itemsAddr!![pos]!!.JibunAddr.length > itemsAddr!![pos]!!.Jibun.length) itemsAddr!![pos]!!.JibunAddr.substring(itemsAddr!![pos]!!.Jibun.length + 1)
        else ""
    }

    fun clickDong(pos: Int){
        for(i in 0 until itemsdong!!.keys.size)
        {
            itemsdong!![i]!!.use = false
        }
        itemsdong!![pos]!!.use = itemsdong!![pos]!!.use != true
        dong = itemsdong!![pos]!!.code.toString()
        onCreateDong()
        searchAddr()
        closeKeyboard()
    }

    fun click_Adress(pos: Int)
    {
        for(i in 0 until itemsAddr!!.keys.size)
        {
            itemsAddr!![i]!!.use = false
        }
        itemsAddr!![pos]!!.use = itemsAddr!![pos]!!.use != true
        addr = itemsAddr!![pos]!!

        title.value = "${addr.CityName} ${addr.CountyName} ${addr.LawTownName} ${addr.Jibun}"
        sub.value = "${addr.CityName} ${addr.CountyName} ${addr.Road}"
        if(addr.JibunAddr!!.length > addr.Jibun!!.length && from.value != 2)
        {
            poi.value = addr.JibunAddr!!.substring(addr.Jibun!!.length + 1)
            detailTxt.value = poi.value
        }
        else if(from.value == 2)
        {
            poi.value = item.CustomerDetailAddr
            detailTxt.value = item.CustomerDetailAddr
        }
        else
        {
            poi.value = ""
            detailTxt.value = poi.value
        }
        onCreateAddr()
        closeKeyboard()
    }

    fun click_cancel()
    {
        Vars.MainsHandler!!.obtainMessage(Finals.CHANGE_CLOSE).sendToTarget()
    }

    fun click_finish(){
        addr.Addr = "${addr.CityName} ${addr.CountyName} ${addr.LawTownName} ${addr.Jibun}"
        addr.detailAddress = detailTxt.value!!
        Vars.MainsHandler!!.obtainMessage(Finals.CHANGE_ADDR,addr).sendToTarget()
        Vars.MainsHandler!!.obtainMessage(Finals.CHANGE_CLOSE).sendToTarget()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun closeKeyboard(){
        foc.value = false
    }

    fun click_choice(){
        from.value = 1
        foc.value = true
        if(mapInstance != null) makeMarker()
    }

    fun makeMarker()
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
            mapInstance!!.moveCamera(CameraUpdate.zoomTo(15.0)) //확대율 조정
        }
    }

    fun click_rechoice(){
        from.value = 0
        CustMarker.map = null
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