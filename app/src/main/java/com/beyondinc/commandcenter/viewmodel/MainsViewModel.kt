package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Alarmdata
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.net.DACallerInterface
import com.beyondinc.commandcenter.util.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.vasone.deliveryalarm.DAClient
import org.json.simple.JSONArray
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainsViewModel : ViewModel() {
    var Tag = "MainsViewModel"
    var mapInstance: NaverMap? = null

    var layer = MutableLiveData<Int>()
    var select = MutableLiveData<Int>()
    var popuptitle = MutableLiveData<String>()
    var checkview = MutableLiveData<Int>()
    var drawer = MutableLiveData<Boolean>()
    var briteLayer = MutableLiveData<Int>()

    var Item : MutableLiveData<Orderdata> = MutableLiveData()
    var showDetail : Boolean = false

    var DetailsSelect = MutableLiveData(Finals.DETAIL_DETAIL)

    var order_count : MutableLiveData<String> = MutableLiveData()
    var rider_count : MutableLiveData<String> = MutableLiveData()

    private lateinit var alarmCallback: (ArrayList<Alarmdata>)-> Unit?

    init {
        Log.e(Tag, "ViewModel Enable Mains")

        val pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
        Vars.Usenick = pref.getBoolean("usenick",  false)
        Vars.UseTime = pref.getBoolean("useTime",  false)
        Vars.UseGana = pref.getBoolean("useGana",  false)
        Vars.UseTTS = pref.getBoolean("useTTS",  false)
        Vars.UseJ = pref.getBoolean("useJ",  false)
        Vars.UseB = pref.getBoolean("useB",  false)
        Vars.UseW = pref.getBoolean("useW",  false)
        Vars.UseC = pref.getBoolean("useC",  false)
        Vars.Bright = pref.getInt("bright", 10)

        Log.e("bbbbbbbbbb", "" + pref.all)

        briteLayer.postValue(Vars.Bright)

        val instanceDACallerInterface = makeDACallerInterfaceInstance(Logindata.LoginId!!, this::alarmCallback)
        DAClient.Initialize(instanceDACallerInterface)
        DAClient.Start()

        layer.postValue(Finals.SELECT_ORDER)
        select.postValue(Finals.SELECT_EMPTY)
        drawer.postValue(false)

        Vars.MainsHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                Log.e("Main Hanldler" , "" + msg.what)
                if(msg.what == Finals.CALL_RIDER) getRiderList()
                else if(msg.what == Finals.INSERT_RIDER) insertRider()
                else if(msg.what == Finals.CALL_CENTER) getCenterList()
                else if(msg.what == Finals.CALL_ORDER) getOrderList()
                else if(msg.what == Finals.CLOSE_CHECK) checkview.postValue(Finals.SELECT_EMPTY)
                else if(msg.what == Finals.ORDER_ITEM_SELECT)
                {
                    Item.postValue(msg.obj as Orderdata?)
                    if(!showDetail) showOrderDetail()
                }
                else if(msg.what == Finals.HTTP_ERROR) HttpError()
                else if(msg.what == Finals.CLOSE_KEYBOARD) closeKeyBoard()
                else if(msg.what == Finals.INSERT_ORDER_COUNT) order_count.postValue(msg.obj as String?)
                else if(msg.what == Finals.INSERT_RIDER_COUNT) rider_count.postValue(msg.obj as String?)
                else if(msg.what == Finals.SET_BRIGHT) setBright()
                else if(msg.what == Finals.CANCEL_MESSAGE) closeMessage()
                else if(msg.what == Finals.SUCCESS_MESSAGE) successMessage(msg.obj as String)
                else if(msg.what == Finals.ORDER_ASSIGN) orderAssign(msg.obj as String)
                else if(msg.what == Finals.ORDER_ASSIGN_LIST) orderAssignList(msg.obj as HashMap<String, ArrayList<String>>)
                else if(msg.what == Finals.ORDER_TOAST_SHOW) showToast(msg.obj as String)
                else if(msg.what == Finals.CLOSE_POPUP) closeDialogHidden()
                else if(msg.what == Finals.CLOSE_DIALOG) closeDialog()
            }
        }
    }

    fun showToast(msg: String){
        closeDialog()
        closeMessage()
        Toast.makeText(Vars.mContext,msg,Toast.LENGTH_LONG).show()
    }

    private fun proceedAlarmCallback(alarmList: ArrayList<Alarmdata>) {
        alarmCallback(alarmList)
    }

    fun makeDACallerInterfaceInstance(loginID: String, callback: (ArrayList<Alarmdata>) -> Unit?
    ): DACallerInterface {
        alarmCallback = callback
        return DACallerInterface(
                Logindata.appID,
                loginID,
                "tcp:dev.stds.co.kr:27070",
                3,
                30,
                this::proceedAlarmCallback,
        )
    }

    private fun alarmCallback(arAlarmInfo: ArrayList<Alarmdata>) {
        for (index in 0 until arAlarmInfo.size) {
            Vars.alarmList.add(arAlarmInfo[index])
        }
    }

    fun HttpError()
    {
        Toast.makeText(Vars.mContext,"서버접속실패",Toast.LENGTH_SHORT).show()
    }

    fun setBright(){
        briteLayer.postValue(Vars.Bright)
        Vars.ItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget()
        Vars.SubItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget()
        Vars.MapHandler!!.obtainMessage(Finals.INSERT_RIDER).sendToTarget()
    }

    fun insertRider()
    {
        getRiderGPS()

        if(!Logindata.RiderList)
        {
            Vars.MainsHandler!!.obtainMessage(Finals.CALL_ORDER).sendToTarget()
            Logindata.RiderList = true
        }
    }

    fun getCenterList()
    {
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp.put(Procedures.CENTER_LIST,MakeJsonParam().makeCenterListParameter(Logindata.LoginId!!))
        Vars.sendList.add(temp)
    }

    fun getOrderList()
    {
        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var ids : java.util.ArrayList<String> = java.util.ArrayList()
        while (it.hasNext())
        {
            ids.add(Vars.centerList[it.next()]!!.centerId)
        }
        var temp : HashMap<String, JSONArray> =  HashMap()
        temp.put(Procedures.ORDER_LIST_IN_CENTER, MakeJsonParam().makeFullOrderListParameter(Logindata.LoginId!!,ids))
        Vars.sendList.add(temp)
    }

    fun getRiderGPS()
    {
        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var ids : java.util.ArrayList<String> = java.util.ArrayList()
        while (it.hasNext())
        {
            ids.add(Vars.centerList[it.next()]!!.centerId)
        }
        var temp : HashMap<String, JSONArray> =  HashMap()
        temp[Procedures.RIDER_LOCATION_IN_CENTER] =
            MakeJsonParam().makeRidersLocationParameter(Logindata.LoginId!!,ids)
        Vars.sendList.add(temp)
    }

    fun getRiderList()
    {
        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var ids : ArrayList<String> = ArrayList()
        while (it.hasNext())
        {
            ids.add(Vars.centerList[it.next()]!!.centerId)
        }
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp[Procedures.RIDER_LIST_IN_CENTER] =
            MakeJsonParam().makeRiderListParameter(Logindata.LoginId!!,ids)
        Vars.sendList.add(temp)
    }

    fun orderAssign(id:String){
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeAssignOrderParameter(Logindata.LoginId!!,Item.value!!.OrderId,Procedures.ChangeStatusType.ASSIGN_RIDER,id)
        Vars.sendList.add(temp)
    }

    fun orderAssignList(rec : HashMap<String,ArrayList<String>>){
        var temp : HashMap<String,JSONArray> =  HashMap()
        var id = rec.keys.iterator().next()
        var order = rec[id]!!
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeAssignOrderListParameter(Logindata.LoginId!!,order,Procedures.ChangeStatusType.ASSIGN_RIDER,id)
        Vars.sendList.add(temp)
    }

    fun orderAssingCancel(){
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeChangeOrderStatusParameter(Logindata.LoginId!!,Item.value!!.OrderId,Procedures.ChangeStatusType.ORDER_ASSIGN_CANCEL,"",Item.value!!.ApprovalType)
        Vars.sendList.add(temp)
    }

    fun orderComplete(){
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeChangeOrderStatusParameter(Logindata.LoginId!!,Item.value!!.OrderId,Procedures.ChangeStatusType.ORDER_FORCE_COMPLETE,Item.value!!.RiderId,Item.value!!.ApprovalType)
        Vars.sendList.add(temp)
    }

    fun orderCancel(){
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeChangeOrderStatusParameter(Logindata.LoginId!!,Item.value!!.OrderId,Procedures.ChangeStatusType.ORDER_CANCEL,Item.value!!.RiderId,Item.value!!.ApprovalType)
        Vars.sendList.add(temp)
    }

    fun orderPaking(){
        var temp : HashMap<String,JSONArray> =  HashMap()
        temp[Procedures.EDIT_ORDER_INFO] = MakeJsonParam().makeOnPickupReadyParameter(Logindata.LoginId!!,Item.value!!.OrderId)
        Vars.sendList.add(temp)
    }

    fun getSelectMap(): Int? {
        return Finals.SELECT_MAP
    }
    fun getSelectOder(): Int? {
        return Finals.SELECT_ORDER
    }
    fun getSelectCheck(): Int? {
        return Finals.SELECT_CHECK
    }
    fun getSelectRider(): Int? {
        return Finals.SELECT_RIDER
    }
    fun getSelectStore(): Int? {
        return Finals.SELECT_STORE
    }
    fun getSelectBrife(): Int? {
        return Finals.SELECT_BRIFE
    }

    fun MapDrOpen(){
        Vars.MapHandler!!.obtainMessage(Finals.MAP_FOR_DOPEN).sendToTarget()
    }

    fun MapOrderOpen(){
        Vars.MapHandler!!.obtainMessage(Finals.MAP_FOR_REMOVE).sendToTarget()
    }

    fun getItemPaymonet() : String?{
        var pay = ""
        if(Item.value!!.ApprovalTypeName == "현금") pay = "(현)"
        else if(Item.value!!.ApprovalTypeName == "카드") pay = "(카)"
        else if(Item.value!!.ApprovalTypeName == "선결제") pay = "(선)"
        pay += " " + Item.value!!.SalesPrice
        return pay
    }
    fun getItemRiderPay() : String?{
        var pay = ""
        if(Item.value!!.ApprovalTypeName == "현금") pay = "(현)"
        else if(Item.value!!.ApprovalTypeName == "카드") pay = "(카)"
        else if(Item.value!!.ApprovalTypeName == "선결제") pay = "(선)"
        pay += " " + Item.value!!.DeliveryFee
        return pay
    }

    fun click_brife() {
        if (select.value == Finals.SELECT_BRIFE) {
            select.postValue(Finals.SELECT_EMPTY)
            Vars.ItemHandler!!.obtainMessage(Finals.DESABLE_SELECT).sendToTarget()
            Vars.ItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        } else {
            select.postValue(Finals.SELECT_BRIFE)
            Vars.ItemHandler!!.obtainMessage(Finals.SELECT_BRIFE).sendToTarget()
        }
    }

    fun click_store() {
        if (select.value == Finals.SELECT_STORE) {
            Vars.ItemHandler!!.obtainMessage(Finals.DESABLE_SELECT).sendToTarget()
            select.postValue(Finals.SELECT_EMPTY)
        }
        else if(select.value == Finals.SELECT_BRIFE) {
            Vars.ItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
            select.postValue(Finals.SELECT_STORE)
            showDialog(2)
        }
        else {
            select.postValue(Finals.SELECT_STORE)
            showDialog(2)
        }
    }

    fun click_rider() {
        if (select.value == Finals.SELECT_RIDER) {
            Vars.ItemHandler!!.obtainMessage(Finals.DESABLE_SELECT).sendToTarget()
            select.postValue(Finals.SELECT_EMPTY)
        }else if(select.value == Finals.SELECT_BRIFE) {
            Vars.ItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
            select.postValue(Finals.SELECT_RIDER)
            showDialog(3)
        }
        else {
            select.postValue(Finals.SELECT_RIDER)
            showDialog(3)
        }
    }

    fun click_map_to_order() {
        if(layer.value != Finals.SELECT_MAP)
        {
            layer.postValue(Finals.SELECT_MAP)
            select.postValue(Finals.SELECT_EMPTY)
            Vars.ItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        }
        else
        {
            layer.postValue(Finals.SELECT_ORDER)
            Vars.MapHandler!!.obtainMessage(Finals.MAP_FOR_DCLOSE).sendToTarget()
            Vars.SubItemHandler!!.obtainMessage(Finals.SELECT_EMPTY).sendToTarget()
        }
    }

    fun click_breifing() {
        if(Vars.multiSelectCnt == 0){
            var toast : Toast = Toast.makeText(Vars.mContext,"선택된 오더가 없습니다.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP,0,300)
            toast.show()
        }
        else showDialog(1)
    }

    fun click_check() {
        if ((layer.value == Finals.SELECT_ORDER || layer.value == Finals.SELECT_MAP) && select.value != Finals.SELECT_BRIFE) {
            if (checkview.value == Finals.SELECT_CHECK) {
                checkview.postValue(Finals.SELECT_EMPTY)
            } else {
                checkview.postValue(Finals.SELECT_CHECK)
            }
        }
    }

    fun click_exit(){
        (Vars.mContext as MainsFun).exit()
    }

    fun click_notice(){
    }

    fun click_setting(){
        (Vars.mContext as MainsFun).setting()
    }

    fun click_center_call(){
    }

    fun click_kakao_call(){
    }

    fun showDialog(txt : Int){
        if(txt == 1) {
            (Vars.mContext as MainsFun).showDialogBrief()
        }
        else if(txt == 2)
        {
            (Vars.mContext as MainsFun).showDialogStore()
        }
        else if(txt == 3)
        {
            (Vars.mContext as MainsFun).showDialogRider()
        }
    }

    fun showOrderDetail() {
        showDetail = true
        (Vars.mContext as MainsFun).showOderdetail()
    }

    fun closeDialog(){
        if(select.value != Finals.SELECT_BRIFE) select.postValue(Finals.SELECT_EMPTY)
        (Vars.mContext as MainsFun).closeDialog()
    }

    fun closeDialogHidden(){
        (Vars.mContext as MainsFun).closeDialog()
    }

    fun closeDetail(){
        (Vars.mContext as MainsFun).closeOderdetail()
        Item.value = null
        showDetail = false
        Vars.ItemHandler!!.obtainMessage(Finals.ORDER_DETAIL_CLOSE).sendToTarget()
    }

    fun closeHistory(){
        (Vars.mContext as MainsFun).closeHistory()
    }

    fun closeMessage(){
        (Vars.mContext as MainsFun).closeMessage()
    }

    fun click_Agent_poi(){
        DetailsSelect.value = Finals.DETAIL_MAP
        (Vars.mContext as MainsFun).detail_Fragment(1)
    }

    fun click_Cust_poi(){
        DetailsSelect.value = Finals.DETAIL_MAP
        (Vars.mContext as MainsFun).detail_Fragment(2)
    }

    fun click_send_agency(){
        (Vars.mContext as MainsFun).send_call(Item.value!!.AgencyPhoneNo)
    }

    fun click_send_cust(){
        (Vars.mContext as MainsFun).send_call(Item.value!!.CustomerPhone)
    }

    fun click_send_rider(){
        (Vars.mContext as MainsFun).send_call(Item.value!!.RiderPhoneNo)
    }

    fun makeMarker(i : Int) {
        if (i == 1) {
            val agencylatitude = Item.value?.AgencyLatitude?.toDouble()
            val agencylongitude = Item.value?.AgencyLongitude?.toDouble()
            if (agencylatitude != null && agencylongitude != null) {
                val agencyPosition = LatLng(agencylatitude, agencylongitude)
                val agencyMarker = Marker()
                agencyMarker.icon = OverlayImage.fromView(
                    FixedMarkerView(
                        Vars.mContext!!,
                        true
                    )
                )
                agencyMarker.position = agencyPosition
                agencyMarker.captionText = Item.value?.AgencyName.toString()
                agencyMarker.setCaptionAligns(Align.Top)
                agencyMarker.map = mapInstance
                mapInstance!!.moveCamera(CameraUpdate.scrollTo(agencyPosition))
            }
        }
        else if(i == 2)
        {
            val agencylatitude = Item.value?.CustomerLatitude?.toDouble()
            val agencylongitude = Item.value?.CustomerLongitude?.toDouble()
            if (agencylatitude != null && agencylongitude != null) {
                val agencyPosition = LatLng(agencylatitude, agencylongitude)
                val agencyMarker = Marker()
                agencyMarker.icon = OverlayImage.fromView(
                    FixedMarkerView(
                        Vars.mContext!!,
                        false
                    )
                )
                agencyMarker.position = agencyPosition
                agencyMarker.captionText = Item.value?.CustomerShortAddr.toString()
                agencyMarker.setCaptionAligns(Align.Top)
                agencyMarker.map = mapInstance
                mapInstance!!.moveCamera(CameraUpdate.scrollTo(agencyPosition))
            }
        }
    }

    fun showHistory(){
        if(DetailsSelect.value == Finals.DETAIL_DETAIL)
        {
            (Vars.mContext as MainsFun).showHistory()
        }
        else if(DetailsSelect.value == Finals.DETAIL_MAP)
        {
            DetailsSelect.value = Finals.DETAIL_DETAIL
            (Vars.mContext as MainsFun).detail_Fragment(0)
        }
    }

    fun showMessage(msg : String){
        (Vars.mContext as MainsFun).showMessage(msg)
    }

    fun showDrawer(){
        if(drawer.value == false) drawer.postValue(true)
        else drawer.postValue(false)
    }

    fun closeKeyBoard(){
        (Vars.mContext as MainsFun).dispatchTouchEvent()
    }

    fun successMessage(msg: String)
    {
        if(msg == "배정취소") orderAssingCancel()
        else if(msg == "오더완료") orderComplete()
        else if(msg == "오더취소") orderCancel()
        else if(msg == "포장상태변경") orderPaking()
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