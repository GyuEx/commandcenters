package com.beyondinc.commandcenter.handler

import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.*
import org.json.simple.JSONArray
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AlarmThread() : Thread() , ThreadFun{

    var isKeep : Boolean = false

    init {
        isKeep = true
    }

    override fun stopThread() {
        isKeep = false
    }

    override fun run() {
        while (isKeep) {
//            try {

                if (Vars.alarmList != null && Vars.alarmList.isNotEmpty()) {

                    var rdata = Vars.alarmList.removeAt(0)

                    Log.e("Alram" , "" + rdata.mnAlarmType + " // " + rdata.mnOrderId)

                    if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_ORDER)
                    {
                        var list : ArrayList<String> = ArrayList()
                        list.add(rdata.mnOrderId.toString())
                        var temp : ConcurrentHashMap<String, JSONArray> = ConcurrentHashMap()
                        temp[Procedures.ORDER_DETAIL] = MakeJsonParam().makeOrderDetailParameter(Logindata.LoginId!!,list)
                        Vars.sendList.add(temp)
                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_DEPOSIT)
                    {

                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_NOTICE)
                    {

                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_ORDER_WORKENV)
                    {

                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_MESSAGE)
                    {

                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_RIDER_WORK_STATE)
                    {
                        if(Vars.riderList.containsKey(rdata.mnOrderId.toString()))
                        {
                            when (rdata.mnDeliveryState)
                            {
                                1 -> Vars.riderList[rdata.mnOrderId.toString()]!!.workingStateCode = Codes.RIDER_ON_WORK
                                2 -> Vars.riderList[rdata.mnOrderId.toString()]!!.workingStateCode = Codes.RIDER_OFF_WORK
                                3 -> Vars.riderList[rdata.mnOrderId.toString()]!!.workingStateCode = Codes.RIDER_ON_EAT
                                4 -> Vars.riderList[rdata.mnOrderId.toString()]!!.workingStateCode = Codes.RIDER_ON_WORK
                            }
                        }
                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_CHATTING)
                    {

                    }
                }
                Thread.sleep(200)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("MainThread",e.toString())
//            }
        }
    }

    fun passing(data : ConcurrentHashMap<String, String>) : Orderdata
    {
        val or = Orderdata()

        or.RcptCenterId = data["RcptCenterId"].toString()
        or.ModDT = data["ModDT"].toString()
        or.EnvColorAssign = data["EnvColorAssign"].toString()
        or.ReceiptSource = data["ReceiptSource"].toString()
        or.ReceiptDT = data["ReceiptDT"].toString()
        or.AgencyGroupColor = data["AgencyGroupColor"].toString()
        or.RiderId = data["RiderId"].toString()
        or.KWeight = data["KWeight"].toString()
        or.CustomerShortAddrNoRoad = data["CustomerShortAddrNoRoad"].toString()
        or.AgencyArrivePlanDT = data["AgencyArrivePlanDT"].toString()
        or.CustomerPhone = data["CustomerPhone"].toString()
        or.DoingOtherCenter = data["DoingOtherCenter"].toString()
        or.ChangePayTypeColor = data["ChangePayTypeColor"].toString()
        or.ApprovalType = data["ApprovalType"].toString()
        or.EnvColorPickup = data["EnvColorPickup"].toString()
        or.AgencyLatitude = data["AgencyLatitude"].toString()
        or.DeviceUniqId = data["DeviceUniqId"].toString()
        or.PackingCompleteYn = data["PackingCompleteYn"].toString()
        or.StoreTypeName = data["StoreTypeName"].toString()
        or.ReceiptDTDisplay = data["ReceiptDTDisplay"].toString()
        or.RiderWorkingCnt = data["RiderWorkingCnt"].toString()
        or.AgencyAddr = data["AgencyAddr"].toString()
        or.ChangePayTypeName = data["ChangePayTypeName"].toString()
        or.DeliveryFeeEtc = data["DeliveryFeeEtc"].toString()
        or.WorkingApprovalType = data["WorkingApprovalType"].toString()
        or.CompleteDT = data["CompleteDT"].toString()
        or.CancelDT = data["CancelDT"].toString()
        or.CustomerDetailAddr = data["CustomerDetailAddr"].toString()
        or.OrderId = data["OrderId"].toString()
        or.ServerTime = data["ServerTime"].toString()
        or.CustomerAddrData = data["CustomerAddrData"].toString()
        or.CenterName = data["CenterName"].toString()
        or.OrderClass = data["OrderClass"].toString()
        or.CustomerShortAddr = data["CustomerShortAddr"].toString()
        or.DeliveryFee = data["DeliveryFee"].toString()
        or.OrderBackColor = data["OrderBackColor"].toString()
        or.AssignCenterId = data["AssignCenterId"].toString()
        or.EndApprovalType = data["EndApprovalType"].toString()
        or.EnvColorCancel = data["EnvColorCancel"].toString()
        or.ShareRegUserId = data["ShareRegUserId"].toString()
        or.AgencyId = data["AgencyId"].toString()
        or.ShareOrderColor = data["ShareOrderColor"].toString()
        or.AgencyLongitude = data["AgencyLongitude"].toString()
        or.AgencyAddrData = data["AgencyAddrData"].toString()
        or.CustomerArriveDT = data["CustomerArriveDT"].toString()
        or.AgencyPhoneNo = data["AgencyPhoneNo"].toString()
        or.ShareOrderYN = data["ShareOrderYN"].toString()
        or.DriverAssignUserId = data["DriverAssignUserId"].toString()
        or.ChangePayTypeShortName = data["ChangePayTypeShortName"].toString()
        or.DeliveryStateName = data["DeliveryStateName"].toString()
        or.CustomerLongAddr = data["CustomerLongAddr"].toString()
        or.SalesPrice = data["SalesPrice"].toString()
        or.StoreMemo = data["StoreMemo"].toString()
        or.EnvColorChangePayType = data["EnvColorChangePayType"].toString()
        or.AfterAssignOrderDelay = data["AfterAssignOrderDelay"].toString()
        or.AssignCancelOrderYn = data["AssignCancelOrderYn"].toString()
        or.EnvColorAssignCancel = data["EnvColorAssignCancel"].toString()
        or.AssignCancelOrderColor = data["AssignCancelOrderColor"].toString()
        or.CustomerLatitude = data["CustomerLatitude"].toString()
        or.AgencyRequestTime = data["AgencyRequestTime"].toString()
        or.AgencyName = data["AgencyName"].toString()
        or.CustomerLongitude = data["CustomerLongitude"].toString()
        or.OrderConfirmDT = data["OrderConfirmDT"].toString()
        or.LoginId = data["LoginId"].toString()
        or.EnvColorReceipt = data["EnvColorReceipt"].toString()
        or.StartApprovalType = data["StartApprovalType"].toString()
        or.DriverAssignDT = data["DriverAssignDT"].toString()
        or.RiderPhoneNo = data["RiderPhoneNo"].toString()
        or.ReceiptId = data["ReceiptId"].toString()
        or.DeliveryState = data["DeliveryState"].toString()
        or.DriverConfirmDT = data["DriverConfirmDT"].toString()
        or.PickupDT = data["PickupDT"].toString()
        or.CenterId = data["CenterId"].toString()
        or.ApprovalTypeName = data["ApprovalTypeName"].toString()
        or.DeliveryDistance = data["DeliveryDistance"].toString()
        or.OuterOrderNo = data["OuterOrderNo"].toString()
        or.EnvColorComplete = data["EnvColorComplete"].toString()
        or.RiderName = data["RiderName"].toString()
        or.StoreTypeId = data[".StoreTypeId"].toString()

        return or
    }
}