package com.beyondinc.commandcenter.handler

import android.os.Handler
import android.os.Message
import android.provider.Telephony
import android.util.Log
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.data.RiderListdata
import com.beyondinc.commandcenter.repository.database.entity.Addrdata
import com.beyondinc.commandcenter.repository.database.entity.Agencydata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class HandlerCallBack : Handler.Callback {

    override fun handleMessage(msg: Message): Boolean {
//        Log.e("DataHandler" , "Message Init // ${msg}")
        // 각 뷰모델마다 what 코드로 분리, 명령코드는 arg1로 분리, arg2는 예비, obj로 필요시 데이터 전달
        // 모든 뷰모델은 View Destory시 null 일수 있으므로, "뷰모델?" 형태로 전달

        when (msg.what)
        {
            Finals.VIEW_ITEM -> when (msg.arg1) // 메인 오더리스트 뷰모델
            {
                Finals.INSERT_ORDER -> Vars.ItemVm?.insertLogic()
                Finals.SELECT_EMPTY -> Vars.ItemVm?.main_filter_select_empty()
                Finals.SELECT_BRIFE -> Vars.ItemVm?.main_filter_select_BRIEF()
                Finals.ORDER_ASSIGN -> Vars.ItemVm?.OrderAssign(msg.obj as String)
                Finals.ORDER_DETAIL_CLOSE -> Vars.ItemVm?.maincloseDetail()
                Finals.STORE_ITEM_SELECT -> Vars.ItemVm?.storeSelect(msg.obj as String)
                Finals.RIDER_ITEM_SELECT -> Vars.ItemVm?.riderSelect(msg.obj as String)
                Finals.DESABLE_SELECT -> Vars.ItemVm?.desableSelect()
                Finals.ALL_CLEAR -> Vars.ItemVm?.allClear()
            }
            Finals.VIEW_ASSIGN -> when (msg.arg1) // 지도 라이더 배정 리스트 뷰모델
            {
                Finals.INSERT_ORDER -> Vars.AssignVm?.insertLogic(msg.obj as ConcurrentHashMap<Int, Orderdata>)
                Finals.SELECT_EMPTY -> Vars.AssignVm?.clearLogic()
                Finals.ORDER_DETAIL_CLOSE -> Vars.AssignVm?.maincloseDetail()
            }
            Finals.VIEW_LOGIN -> when (msg.arg1) // 로그인 뷰모델
            {
                Finals.LOGIN_SUCESS -> Vars.LoginVm?.LoginSucess()
                Finals.APK_UPDATE -> Vars.LoginVm?.downloadApk()
                Finals.LOGIN_FAIL -> Vars.LoginVm?.LoginFail()
                Finals.CHANGE_PASSWORD -> Vars.LoginVm?.changepassword(msg.obj as String)
                Finals.CHANGE_CLOSE -> Vars.LoginVm?.closechange()
                Finals.SHOW_MESSAGE -> Vars.LoginVm?.showMSG(msg.obj as String)
            }
            Finals.VIEW_SUBITEM -> when (msg.arg1) // 지도 하단 접수목록 뷰모델
            {
                Finals.INSERT_ORDER -> Vars.SubItemVm?.insertLogic()
                Finals.SELECT_ORDER -> Vars.SubItemVm?.select!!.value = Finals.SELECT_ORDER
                Finals.SELECT_EMPTY -> Vars.SubItemVm?.selectEmpte()
                Finals.ORDER_ASSIGN_LIST -> Vars.SubItemVm?.orderassignlist(msg.obj as String)
                Finals.ALL_CLEAR -> Vars.SubItemVm?.allClear()
            }
            Finals.VIEW_SUBRIDER -> when (msg.arg1) // 지도 좌측 드로워 라이더 목록 뷰모델
            {
                Finals.INSERT_RIDER -> Vars.SubRiderVm?.insertLogic(msg.obj as Riderdata)
                Finals.SELECT_RIDER -> Vars.SubRiderVm?.select!!.value = Finals.SELECT_RIDER
                Finals.SELECT_EMPTY -> Vars.SubRiderVm?.selectEmpty()
                Finals.MAP_FOR_CALL_RIDER -> Vars.SubRiderVm?.getRider(msg.obj)
                Finals.REMOVE_RIDER_MARKER -> Vars.SubRiderVm?.removeRider(msg.obj as Riderdata)
                Finals.MAP_FOR_REFRASH -> Vars.SubRiderVm?.refrashrider()
            }
            Finals.VIEW_CHECK -> when (msg.arg1) // 센터목록 뷰모델
            {
                Finals.INSERT_STORE -> Vars.CheckVm?.insertStore()
            }
            Finals.VIEW_MAP -> when (msg.arg1) // 메인 지도 뷰모델
            {
                Finals.CREATE_RIDER_MARKER -> if(msg.obj != null) Vars.MapVm?.createRider(msg.obj as Riderdata)
                Finals.UPDATE_RIDER_MARKER -> if(msg.obj != null) Vars.MapVm?.updateRider(msg.obj as Riderdata)
                Finals.REMOVE_RIDER_MARKER -> if(msg.obj != null) Vars.MapVm?.removeRider(msg.obj as Riderdata)
                Finals.MAP_FOR_DOPEN -> Vars.MapVm?.OpenDrawer()
                Finals.MAP_FOR_DCLOSE -> Vars.MapVm?.dclose()
                Finals.MAP_MOVE_FOCUS -> Vars.MapVm?.MapFocusSet(msg.obj as Riderdata)
                Finals.MAP_FOR_REMOVE -> Vars.MapVm?.CancelRider()
                Finals.SELECT_ORDER -> Vars.MapVm?.selectOr()
                Finals.SELECT_EMPTY -> Vars.MapVm?.emptyOr()
                Finals.INSERT_ORDER_COUNT -> Vars.MapVm?.subitemsize!!.value = msg.obj as Int
                Finals.MAP_FOR_ASSIGN_CREATE -> Vars.MapVm?.createAssign(msg.obj as Orderdata)
                Finals.MAP_FOR_ASSIGN_REMOVE -> Vars.MapVm?.removeAssign(msg.obj as Orderdata)
                Finals.ORDER_ASSIGN -> Vars.MapVm?.to_assgin_click()
            }
            Finals.VIEW_ADDRESS -> when (msg.arg1) // 주소검색 뷰모델
            {
                Finals.SEARCH_ADDR -> Vars.AddressVm?.insertAddr()
                Finals.MESSAGE_ADDR -> Vars.AddressVm?.showMsg()
                Finals.GET_DELIVERY_FEE -> Vars.AddressVm?.initDeliFee(msg.obj as HashMap<String, String>)
                Finals.INSERT_NEW_ORDER -> Vars.AddressVm?.insertNewOrder()
            }
            Finals.VIEW_AGENCY -> when (msg.arg1)
            {
                Finals.INSERT_ORDER -> Vars.AgencyVm?.insertLogic()
                Finals.ALL_CLEAR -> Vars.AgencyVm?.clear()
            }
            Finals.VIEW_RIDER -> when (msg.arg1)
            {
                Finals.INSERT_ORDER -> Vars.RiderListVm?.insertLogic()
                Finals.ALL_CLEAR -> Vars.RiderListVm?.clear()
            }
            Finals.VIEW_MAIN -> when (msg.arg1) //메인 뷰모델
            {
                Finals.CALL_RIDER -> Vars.MainVm?.getRiderList()
                Finals.INSERT_RIDER -> Vars.MainVm?.insertRider()
                Finals.CALL_CENTER -> Vars.MainVm?.getCenterList()
                Finals.CALL_ORDER -> Vars.MainVm?.getOrderList()
                Finals.CLOSE_CHECK -> Vars.MainVm?.closecheck()
                Finals.ORDER_ITEM_SELECT -> Vars.MainVm?.showOrderDetail(msg.obj as Orderdata,msg.arg2)
                Finals.AGENCY_ITEM_SELECT -> Vars.MainVm?.showAgencyDetail(msg.obj as Agencydata,msg.arg2)
                Finals.RIDER_ITEM_SELECT -> Vars.MainVm?.showRiderDetail(msg.obj as RiderListdata,msg.arg2)
                Finals.HTTP_ERROR -> Vars.MainVm?.HttpError()
                Finals.CLOSE_KEYBOARD -> Vars.MainVm?.closeKeyBoard()
                Finals.INSERT_ORDER_COUNT -> Vars.MainVm?.order_count!!.postValue(msg.obj as String)
                Finals.INSERT_RIDER_COUNT -> Vars.MainVm?.rider_count!!.postValue(msg.obj as String)
                Finals.SET_BRIGHT -> Vars.MainVm?.setBright()
                Finals.CANCEL_MESSAGE -> Vars.MainVm?.closeMessage()
                Finals.SUCCESS_MESSAGE -> Vars.MainVm?.successMessage(msg.obj as String)
                Finals.ORDER_ASSIGN -> Vars.MainVm?.orderAssign(msg.obj as String)
                Finals.ORDER_ASSIGN_LIST -> Vars.MainVm?.orderAssignList(msg.obj as ConcurrentHashMap<String, ArrayList<String>>)
                Finals.ORDER_TOAST_SHOW -> Vars.MainVm?.showToast(msg.obj as String)
                Finals.CLOSE_POPUP -> Vars.MainVm?.closeDialogHidden(msg.obj as String)
                Finals.CLOSE_DIALOG -> Vars.MainVm?.closeDialog()
                Finals.SEND_TELEPHONE -> Vars.MainVm?.send_call(msg.obj as String)
                Finals.MAP_FOR_SOPEN -> Vars.MainVm?.showSelectDialog(msg.obj as Orderdata)
                Finals.CALL_GPS -> Vars.MainVm?.getRiderGPS()
                Finals.SEND_ITEM -> Vars.MainVm?.getItemsend(msg.obj as Orderdata)
                Finals.CHANGE_ADDRESS -> Vars.MainVm?.showAddress()
                Finals.NEW_ASSIGN -> Vars.MainVm?.newAssgint(msg.obj as Agencydata)
                Finals.CHECK_TIME -> Vars.MainVm?.checkOrderLastTime()
                Finals.CHECK_COUNT -> Vars.MainVm?.checkOrderCount()
                Finals.CONN_ALRAM -> Vars.MainVm?.connenctAlram()
                Finals.CLOSE_DETAIL -> Vars.MainVm?.closeDetail()
                Finals.SHOW_LOADING -> Vars.MainVm?.showLoading()
                Finals.CLOSE_LOADING -> Vars.MainVm?.closeLoading()
                Finals.DISCONN_ALRAM -> Vars.MainVm?.disconnectAlram()
                Finals.SHOW_MESSAGE -> Vars.MainVm?.showMessage(msg.obj as String , "0")
                Finals.CHANGE_CLOSE -> Vars.MainVm?.changeClose()
                Finals.SEARCH_ADDR -> Vars.MainVm?.getAddress(msg.obj as HashMap<Int, String>)
                Finals.SEARCH_NEW_ADDR -> Vars.MainVm?.getAddressNew(msg.obj as HashMap<Int, String>)
                Finals.CHANGE_ADDR -> Vars.MainVm?.changeAddr(msg.obj as Addrdata)
                Finals.SHOW_ADDR -> Vars.MainVm?.insertDetailAddr()
                Finals.SHOW_NEW_ASSIGN -> Vars.MainVm?.insertNewAssign()
                Finals.GET_DELIVERY_FEE -> Vars.MainVm?.DeliveryFee(msg.obj as Addrdata)
                Finals.INSERT_NEW_ORDER -> Vars.MainVm?.insertNewOrderRegSend(msg.obj as HashMap<String, String>)
                Finals.LOG_OUT -> Vars.MainVm?.Logout()
                Finals.RE_LOGIN -> Vars.MainVm?.Re_login(msg.arg2)
                Finals.SEND_SMS -> Vars.MainVm?.sendSMS(msg.obj as String)
            }
        }

        return true
    }
}
