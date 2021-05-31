package com.beyondinc.commandcenter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Alarmdata
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.net.DACallerInterface
import com.beyondinc.commandcenter.repository.database.entity.Addrdata
import com.beyondinc.commandcenter.repository.database.entity.Dongdata
import com.beyondinc.commandcenter.util.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.vasone.deliveryalarm.DAClient
import org.json.simple.JSONArray
import java.util.concurrent.ConcurrentHashMap


class MainsViewModel : ViewModel() {
    var Tag = "MainsViewModel"
    var mapInstance: NaverMap? = null

    var layer = MutableLiveData<Int>()
    var select = MutableLiveData<Int>()
    var checkview = MutableLiveData<Int>()
    var drawer = MutableLiveData<Boolean>()
    var briteLayer = MutableLiveData<Int>()

    var proTxt = MutableLiveData<String>()

    var Item : MutableLiveData<Orderdata> = MutableLiveData()
    var showDetail : Boolean = false
    var payselect = 1 // 1:물품금액 , 2: 배달금액
    var dropitem = ""
    var pay : MutableLiveData<String> = MutableLiveData()

    var DetailsSelect = MutableLiveData(Finals.DETAIL_DETAIL)

    var order_count : MutableLiveData<String> = MutableLiveData()
    var rider_count : MutableLiveData<String> = MutableLiveData()

    private lateinit var alarmCallback: (ArrayList<Alarmdata>)-> Unit?

    init {
        Log.e(Tag, "ViewModel Enable Mains")

        val pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
        Vars.Usenick = pref.getBoolean("usenick", false)
        Vars.UseTime = pref.getBoolean("useTime", false)
        Vars.UseGana = pref.getBoolean("useGana", false)
        Vars.UseTTS = pref.getBoolean("useTTS", false)
        Vars.UseJ = pref.getBoolean("useJ", false)
        Vars.UseB = pref.getBoolean("useB", false)
        Vars.UseW = pref.getBoolean("useW", false)
        Vars.UseC = pref.getBoolean("useC", false)
        Vars.Bright = pref.getInt("bright", 10)
        Vars.FontSize = pref.getInt("fontsize", 15)

        briteLayer.value = Vars.Bright

        proTxt.value = "서버에 접속중이예요!"

        layer.postValue(Finals.SELECT_ORDER)
        select.postValue(Finals.SELECT_EMPTY)
        // 위 두줄 포스트벨류로 안넣으면 초기 맵 선로딩이 안될 경우가 더 많음(라이브데이터 속도차이)
        drawer.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Vars.MainVm = null
    }

    fun insertAddr(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ADDRESS,Finals.INSERT_ADDR,0,Item.value).sendToTarget()
    }

    fun insertDetailAddr()
    {
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ADDRESS,Finals.INSERT_ADDR,0,Item.value).sendToTarget()
        (Vars.mContext as MainsFun).closeMessage()
        (Vars.mContext as MainsFun).showAddress()
    }

    fun changeAddr(addrdata: Addrdata) {
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.EDIT_ORDER_INFO] = MakeJsonParam().makeChangeDeliveryAddressParameter(Logindata.LoginId!!, Item.value!!.OrderId, addrdata.Addr, addrdata.detailAddress,addrdata.Jibun,addrdata.Road,addrdata.LawTownName,addrdata.BuildingManageNo,addrdata.Latitude, addrdata.Longitude)
        Vars.sendList.add(temp)
    }

    fun showLoading(){
        (Vars.mContext as MainsFun).showLoading()
    }

    fun closeLoading(){
        proTxt.value = "완료되었어요!"
        (Vars.mContext as MainsFun).closeLoading()
    }

    fun connenctAlram(){
        proTxt.value = "알람서버에 요청중이예요!"
        if(!Vars.daclient)
        {
            var instanceDACallerInterface = makeDACallerInterfaceInstance(Logindata.LoginId!!, this::alarmCallback)
            DAClient.Initialize(instanceDACallerInterface)
            DAClient.Start()
            Vars.daclient = true
        }
        closeLoading()
    }

    fun disconnectAlram(){
        if(Vars.daclient)
        {
            DAClient.Stop()
            Vars.daclient = false
        }
    }

    fun checkOrderLastTime(){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.ORDER_LIST_IN_CENTER] = MakeJsonParam().makeChangedOrderListParameter(Logindata.LoginId!!, Vars.centerLastTime)
        Vars.sendList.add(temp)
    }

    fun getAddress(value : HashMap<Int,String>){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.EDIT_ORDER_INFO] = MakeJsonParam().makeAddressListParameter(Logindata.LoginId!!, Item.value!!.OrderId, Item.value!!.RcptCenterId, Item.value!!.AgencyId,value[0].toString(),value[1].toString(),value[2].toString())
        Vars.sendList.add(temp)
    }

    fun checkOrderCount(){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        var it:Iterator<String> = Vars.centerList.keys.iterator()
        while (it.hasNext())
        {
            var its = it.next()
            var cnt = 0
            var itt:Iterator<String> = Vars.orderList.keys.iterator()
            while (itt.hasNext())
            {
                var itts = itt.next()
                if(Vars.orderList[itts]!!.AssignCenterId == its || Vars.orderList[itts]!!.RcptCenterId == its) cnt++
                // RcptCenter 불변 ,, AssginCenterId 가변 ,, 추가적인 요소는 없으면 됨
            }
            Vars.centerOrderCount[its] = cnt.toString()
        }
        temp[Procedures.ORDER_LIST_IN_CENTER] = MakeJsonParam().makeServerOrderListCountParameter(Logindata.LoginId!!, Vars.centerOrderCount)
        Vars.sendList.add(temp)
    }

    fun showAddress(){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.EDIT_ORDER_INFO] = MakeJsonParam().makeAgencyTownListParameter(Logindata.LoginId!!, Item.value!!.OrderId, Item.value!!.RcptCenterId, Item.value!!.AgencyId)
        Vars.sendList.add(temp)
    }

    fun changePay(sub: Int){
        payselect = sub
        (Vars.mContext as MainsFun).showPayment()
    }

    fun changeClose(){
        pay.value = ""
        (Vars.mContext as MainsFun).changeClose()
    }

    fun successPayment(){
        if(pay.value!!.length > 6)
        {
            Toast.makeText(Vars.mContext,"백만원 이상은 좀 과하지 않나요 T^T",Toast.LENGTH_SHORT).show()
            return
        }
        if(payselect == 1)
        {
            var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
            temp[Procedures.EDIT_ORDER_INFO] = MakeJsonParam().makeChangeSalesPriceParameter(Logindata.LoginId!!, Item.value!!.OrderId, pay.value!!)
            Vars.sendList.add(temp)
        }
        else if(payselect == 2)
        {
            var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
            temp[Procedures.EDIT_ORDER_INFO] = MakeJsonParam().makeAddDeliveryFeeParameter(Logindata.LoginId!!, Item.value!!.OrderId, pay.value!!)
            Vars.sendList.add(temp)
        }
        changeClose()
    }

    fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        if(position == 0) dropitem = "현금"
        else if(position == 1) dropitem = "카드"
        else if(position == 2) dropitem = "선결제"
    }

    fun getItemsend(obj: Orderdata){
        Item.value = obj
    }

    fun showToast(msg: String){
        closeDialog()
        closeMessage()
        Toast.makeText(Vars.mContext, msg, Toast.LENGTH_LONG).show()
    }

    fun mapfordetail(){
        showDetail = true
        (Vars.mContext as MainsFun).showOderdetail()
        (Vars.mContext as MainsFun).closeSelect()
    }

    fun mapforchange(){
        showDialog(1)
        (Vars.mContext as MainsFun).closeSelect()
    }

    fun mapforcancel(){
        showMessage("배정취소", "0")
        (Vars.mContext as MainsFun).closeSelect()
    }

    fun mapfororcancel(){
        showMessage("오더취소", "0")
        (Vars.mContext as MainsFun).closeSelect()
    }

    fun showSelectDialog(msg: Any?){
        Item.value = msg as Orderdata
        (Vars.mContext as MainsFun).showSelect()
    }

    fun closeSelectDialog(){
        Item.value = null
        (Vars.mContext as MainsFun).closeSelect()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ASSIGN,Finals.ORDER_DETAIL_CLOSE,0).sendToTarget()
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
                Vars.mContext!!.resources.getString(R.string.alarm_url),
                Vars.mContext!!.resources.getInteger(R.integer.timeout_connect_default),
                Vars.mContext!!.resources.getInteger(R.integer.timeout_read_default),
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
        proTxt.value = "인터넷 연결상태가 올바르지 않습니다.\n연결이 회복되면 자동으로 사라집니다."
        showLoading()
    }

    fun setBright(){
        briteLayer.value = Vars.Bright
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_CHECK,Finals.INSERT_STORE,0).sendToTarget()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.INSERT_ORDER,0).sendToTarget()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.INSERT_RIDER,0).sendToTarget()
    }

    fun insertRider()
    {
        getRiderGPS()
        if(!Logindata.RiderList)
        {
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CALL_ORDER,0).sendToTarget()
            Logindata.RiderList = true
        }
    }

    fun getCenterList()
    {
        proTxt.value = "센터를 가져오고 있어요!"
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp.put(Procedures.CENTER_LIST, MakeJsonParam().makeCenterListParameter(Logindata.LoginId!!))
        Vars.sendList.add(temp)
    }

    fun getOrderList()
    {
        proTxt.value = "오더를 가져오고 있어요!"
        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var ids : java.util.ArrayList<String> = java.util.ArrayList()
        while (it.hasNext())
        {
            ids.add(Vars.centerList[it.next()]!!.centerId)
        }
        //ids.add("147")
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.ORDER_LIST_IN_CENTER] = MakeJsonParam().makeFullOrderListParameter(Logindata.LoginId!!, ids)
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
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.RIDER_LOCATION_IN_CENTER] =
            MakeJsonParam().makeRidersLocationParameter(Logindata.LoginId!!, ids)
        Vars.sendList.add(temp)
    }

    fun getRiderList()
    {
        proTxt.value = "라이더를 가져오고 있어요!"

        var it : Iterator<String> = Vars.centerList.keys.iterator()
        var ids : ArrayList<String> = ArrayList()
        while (it.hasNext())
        {
            ids.add(Vars.centerList[it.next()]!!.centerId)
        }
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.RIDER_LIST_IN_CENTER] =
            MakeJsonParam().makeRiderListParameter(Logindata.LoginId!!, ids)
        Vars.sendList.add(temp)
    }

    fun orderAssign(id: String){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeAssignOrderParameter(Logindata.LoginId!!, Item.value!!.OrderId, Procedures.ChangeStatusType.ASSIGN_RIDER, id)
        Vars.sendList.add(temp)
    }

    fun orderAssignList(rec: ConcurrentHashMap<String, ArrayList<String>>){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        var id = rec.keys.iterator().next()
        var order = rec[id]!!
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeAssignOrderListParameter(Logindata.LoginId!!, order, Procedures.ChangeStatusType.ASSIGN_RIDER, id)
        Vars.sendList.add(temp)
    }

    fun orderAssingCancel(){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeChangeOrderStatusParameter(Logindata.LoginId!!, Item.value!!.OrderId, Procedures.ChangeStatusType.ORDER_ASSIGN_CANCEL, "", Item.value!!.ApprovalType)
        Vars.sendList.add(temp)
    }

    fun orderComplete(){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeChangeOrderStatusParameter(Logindata.LoginId!!, Item.value!!.OrderId, Procedures.ChangeStatusType.ORDER_FORCE_COMPLETE, Item.value!!.RiderId, Item.value!!.ApprovalType)
        Vars.sendList.add(temp)
    }

    fun orderCancel(){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.CHANGE_DELIVERY_STATUS] = MakeJsonParam().makeChangeOrderStatusParameter(Logindata.LoginId!!, Item.value!!.OrderId, Procedures.ChangeStatusType.ORDER_CANCEL, Item.value!!.RiderId, Item.value!!.ApprovalType)
        Vars.sendList.add(temp)
    }

    fun orderPaking(){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.EDIT_ORDER_INFO] = MakeJsonParam().makeOnPickupReadyParameter(Logindata.LoginId!!, Item.value!!.OrderId)
        Vars.sendList.add(temp)
    }

    fun orderTown(){
        var temp : ConcurrentHashMap<String, JSONArray> =  ConcurrentHashMap()
        temp[Procedures.EDIT_ORDER_INFO] = MakeJsonParam().makeOnPickupReadyParameter(Logindata.LoginId!!, Item.value!!.OrderId)
        Vars.sendList.add(temp)
    }

    fun getSelectMap(): Int? {
        return Finals.SELECT_MAP
    }
    fun getSelectOder(): Int? {
        return Finals.SELECT_ORDER
    }
    fun getSelectBrife(): Int? {
        return Finals.SELECT_BRIFE
    }

    fun MapDrOpen(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.MAP_FOR_DOPEN,0).sendToTarget()
    }

    fun MapOrderOpen(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.MAP_FOR_REMOVE,0).sendToTarget()
    }

    fun click_brife() {
        if (select.value == Finals.SELECT_BRIFE) {
            select.value = Finals.SELECT_EMPTY
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.DESABLE_SELECT,0).sendToTarget()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.SELECT_EMPTY,0).sendToTarget()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
        } else {
            select.value = Finals.SELECT_BRIFE
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.SELECT_BRIFE,0).sendToTarget()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
        }
    }

    fun click_store() {
        if (select.value == Finals.SELECT_STORE) {
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.DESABLE_SELECT,0).sendToTarget()
            select.value = Finals.SELECT_EMPTY
        }
        else if(select.value == Finals.SELECT_BRIFE) {
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.SELECT_EMPTY,0).sendToTarget()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
            select.value = Finals.SELECT_STORE
            showDialog(2)
        }
        else {
            select.value = Finals.SELECT_STORE
            showDialog(2)
        }
    }

    fun click_rider() {
        if (select.value == Finals.SELECT_RIDER) {
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.DESABLE_SELECT,0).sendToTarget()
            select.value = Finals.SELECT_EMPTY
        }else if(select.value == Finals.SELECT_BRIFE) {
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.SELECT_EMPTY,0).sendToTarget()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
            select.value = Finals.SELECT_RIDER
            showDialog(3)
        }
        else {
            select.value = Finals.SELECT_RIDER
            showDialog(3)
        }
    }

    fun click_map_to_order() {
        if(layer.value != Finals.SELECT_MAP)
        {
            layer.value = Finals.SELECT_MAP
            Vars.mLayer = Finals.SELECT_MAP // 쓰레드가 현재 메인레이어를 뭘보고 있는지 알고싶어함
            select.value = Finals.SELECT_EMPTY
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.SELECT_EMPTY,0).sendToTarget()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.INSERT_ORDER,0).sendToTarget()
            // empty를 먼저 던져줘야 맵이 초기화가 됨
        }
        else
        {
            layer.value = Finals.SELECT_ORDER
            Vars.mLayer = Finals.SELECT_ORDER // 쓰레드가 현재 메인레이어를 뭘보고 있는지 알고싶어함
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.SELECT_EMPTY,0).sendToTarget()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.MAP_FOR_DCLOSE,0).sendToTarget()
            // empty를 먼저 던져줘야 맵이 초기화가 됨
        }
    }

    fun click_breifing() {
        if(Vars.multiSelectCnt == 0){
            var toast : Toast = Toast.makeText(Vars.mContext, "선택된 오더가 없습니다.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP, 0, 300)
            toast.show()
        }
        else showDialog(1)
    }

    fun click_check() {
        if ((layer.value == Finals.SELECT_ORDER || layer.value == Finals.SELECT_MAP) && select.value != Finals.SELECT_BRIFE) {
            if (checkview.value == Finals.SELECT_CHECK) {
                checkview.value = Finals.SELECT_EMPTY
            } else {
                checkview.value = Finals.SELECT_CHECK
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

    fun showDialog(txt: Int){
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

    fun showOrderDetail(msg: Any?) {
        if(!showDetail) // 창을 새로 띄우기 보단 안띄우고 갱신 시키기 위함
        {
            showDetail = true
            DetailsSelect.value = Finals.DETAIL_DETAIL
            (Vars.mContext as MainsFun).showOderdetail()
        }
        Item.value = msg as Orderdata
    }

    fun closeDialog(){
        if(select.value != Finals.SELECT_BRIFE) select.value = Finals.SELECT_EMPTY
        (Vars.mContext as MainsFun).closeDialog()
    }

    fun closeDialogHidden(){
        (Vars.mContext as MainsFun).closeDialog()
    }

    fun closeDetail(){
        (Vars.mContext as MainsFun).closeOderdetail()
        //Item.value = null
        showDetail = false
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.ORDER_DETAIL_CLOSE,0).sendToTarget()
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ASSIGN,Finals.ORDER_DETAIL_CLOSE,0).sendToTarget()
        (Vars.mContext as MainsFun).closeSelect()
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

    fun send_call(num: String){
        (Vars.mContext as MainsFun).send_call(num)
        closeMessage()
    }

    fun makeMarker(i: Int) {
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

    fun showMessage(msg: String, num: String){
        (Vars.mContext as MainsFun).showMessage(msg, num)
    }

    fun showDrawer(){
        drawer.value = drawer.value == false
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